package therustyknife.timer.Fragments;


import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.ArrayList;

import therustyknife.timer.R;
import therustyknife.timer.TimeUtil;
import therustyknife.timer.Timer;
import therustyknife.timer.TimerStats;
import therustyknife.timer.Util;


// the fragment that shows the days with timelines
public class CalendarDaySmallFragment extends Fragment {
    // again, a hack around the fact that we can't pass in arguments to the constructor
    private static int argDayOffset;
    // determines the day that will be shown, 0 being today and negative the days that had passed
    // positive numbers will cause the time sum to show "--"
    private int dayOffset;


    public CalendarDaySmallFragment(){
        // set up the fields
        this.dayOffset = argDayOffset;
        argDayOffset = 0;
    }

    public static CalendarDaySmallFragment newInstance(int dayOffset) {
        // hack-pass the day offset to the constructor, ugh
        argDayOffset = dayOffset;

        return new CalendarDaySmallFragment();
    }
    public static CalendarDaySmallFragment newInstance(){ return newInstance(0); } // default day to today


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day_small, container, false);

        // set the text into the day name
        ((TextView) view.findViewById(R.id.day_small_name)).setText(getResources().getStringArray(R.array.day_names)[DateTime.now().dayOfWeek().get() + dayOffset - 1]);

        // set the time value into the other TextView
        TextView tv = (TextView) view.findViewById(R.id.day_small_time);
        if (dayOffset > 0) tv.setText("--"); // set the default if the day hasn't happened yet
        else {
            // sum up all the sessions that had happened that day
            int res = 0;
            for (Timer t : Timer.getList()) {
                res += t.getStats().getRunInDay(dayOffset);
            }
            tv.setText(Util.formatTime(res));
        }

        // listen for the onGlobalLayout event and draw the bars there, since we need the layout to be set up for that
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                drawBars();

                // remove the listener once the bars have been drawn
                ViewTreeObserver obs = getView().getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });

        return view;
    }


    // draws the bars of the current day into the view
    protected void drawBars(){
        // get our container view
        ViewGroup view = (ViewGroup) getView().findViewById(R.id.timeline);

        // get the sessions
        ArrayList<TimerStats.Session> sessions = new ArrayList<>();
        for (Timer t : Timer.getList())
            sessions.addAll(t.getStats().getSessionsInDay(dayOffset));

        // set the minimum height of our bars to the same as their width to make them circular in their smallest state
        float minHeight = getResources().getDimension(R.dimen.bar_width);

        // draw the bars
        for (TimerStats.Session s : sessions){
            // inflate the bar view and add it to the container view
            View barView = getActivity().getLayoutInflater().inflate(R.layout.bar, null);
            view.addView(barView);

            // figure out the height that should be used for this bar
            float height = view.getHeight() * TimeUtil.getDayPercent(s.getLength());
            if (height < minHeight) height = minHeight;

            // get a reference to the actual View with the bar background
            View bar = barView.findViewById(R.id.bar);

            // set the bars height
            ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.height = (int)height;
            bar.setLayoutParams(params);

            // set the bars back color
            GradientDrawable bg = (GradientDrawable) bar.getBackground();
            bg.setColor(s.getTimer().getColor());

            // figure out the vertical position for this bar
            float y = view.getHeight() * TimeUtil.getDayProgressPercent(s.getStartedAt());
            if (y > (view.getHeight() - height)) y = view.getHeight() - height;

            // set the position of the bar
            barView.setY(y);
        }

        // reset the timeline background to prevent that weird "setColor() affecting Views that it shouldn't" bug
        GradientDrawable bg = (GradientDrawable) getView().findViewById(R.id.timeline_bg).getBackground();
        bg.setColor(getResources().getColor(R.color.time_line));
    }
}
