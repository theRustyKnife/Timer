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
    // the onClick listener
    private OnDayClickListener onClickListener;

    // determines the day that will be shown, 0 being today and negative the days that had passed
    // positive numbers will cause the time sum to show "--"
    private int dayOffset;

    // the height that'll be used for this fragment
    private int height;

    private View dayTitle;
    private View timeDisplay;

    private boolean setVisibleOnCreate;

    public static CalendarDaySmallFragment newInstance(int dayOffset, int height, boolean textVisible) {
        // hack-pass the day offset and height to the constructor, ugh
        CalendarDaySmallFragment res = new CalendarDaySmallFragment();

        res.setDayOffset(dayOffset);
        res.setHeight(height);

        res.setVisibleOnCreate = textVisible;

        return res;
    }
    public static CalendarDaySmallFragment newInstance(int dayOffset, int height){ return newInstance(dayOffset, height, true); }
    public static CalendarDaySmallFragment newInstance(int dayOffset){ return newInstance(dayOffset, (int)Util.context.getResources().getDimension(R.dimen.day_small_default_height)); }
    public static CalendarDaySmallFragment newInstance(){ return newInstance(0); } // default day to today


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day_small, container, false);

        // pass the click events
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(dayOffset);
            }
        });

        // get references to the container view and background
        View timeline = view.findViewById(R.id.timeline);
        View bg = view.findViewById(R.id.timeline_bg);

        // get references to the TextViews
        dayTitle = view.findViewById(R.id.day_small_name);
        timeDisplay = view.findViewById(R.id.day_small_time);

        // set the height of the container and background - this will resize the entire view
        ViewGroup.LayoutParams paramsTL = timeline.getLayoutParams();
        ViewGroup.LayoutParams paramsBG = bg.getLayoutParams();
        paramsTL.height = height;
        paramsBG.height = height;
        timeline.setLayoutParams(paramsTL);
        bg.setLayoutParams(paramsBG);

        // set the text into the day name
        ((TextView) dayTitle).setText(getResources().getStringArray(R.array.day_names)[DateTime.now().dayOfWeek().get() + dayOffset - 1]);

        // set the time value into the other TextView
        TextView tv = (TextView) timeDisplay;
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

        // make the TextViews invisible if desired
        setDetailsVisible(setVisibleOnCreate);

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


    public void setOnDayClickListener(OnDayClickListener listener){ this.onClickListener = listener; }


    private void setDayOffset(int dayOffset){ this.dayOffset = dayOffset; }

    private void setHeight(int height){ this.height = height; }


    // make the text / invisible
    public void setDetailsVisible(boolean visible){
        if (!visible){
            timeDisplay.setVisibility(View.GONE);
            dayTitle.setVisibility(View.GONE);
        }else{
            timeDisplay.setVisibility(View.VISIBLE);
            dayTitle.setVisibility(View.VISIBLE);
        }
    }


    public interface OnDayClickListener{
        void onClick(int dayOffset);
    }
}
