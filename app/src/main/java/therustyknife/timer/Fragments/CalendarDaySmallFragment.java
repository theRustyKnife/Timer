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

public class CalendarDaySmallFragment extends Fragment {
    private static int argDayOffset;
    private int dayOffset;


    public CalendarDaySmallFragment(){
        this.dayOffset = argDayOffset;
        argDayOffset = 0;
    }

    public static CalendarDaySmallFragment newInstance(int dayOffset) {
        argDayOffset = dayOffset;

        return new CalendarDaySmallFragment();
    }
    public static CalendarDaySmallFragment newInstance(){
        return newInstance(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day_small, container, false);

        // set the text
        ((TextView) view.findViewById(R.id.day_small_name)).setText(getResources().getStringArray(R.array.day_names)[DateTime.now().dayOfWeek().get() + dayOffset - 1]);
        TextView tv = (TextView) view.findViewById(R.id.day_small_time);
        if (dayOffset > 0) tv.setText("--");
        else {
            int res = 0;
            for (Timer t : Timer.getList()) {
                res += t.getStats().getRunInDay(dayOffset);
            }
            tv.setText(Util.formatTime(res));
        }

        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                drawBars();

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

    protected void drawBars(){
        ViewGroup view = (ViewGroup) getView().findViewById(R.id.timeline);

        // get the sessions
        ArrayList<TimerStats.Session> sessions = new ArrayList<>();
        for (Timer t : Timer.getList()){
            sessions.addAll(t.getStats().getSessionsInDay(dayOffset));
        }

        Log.d(Util.TAG, "number of sessions: " + sessions.size());

        float minHeight = getResources().getDimension(R.dimen.bar_width);

        // draw the bars
        for (TimerStats.Session s : sessions){
            View barView = getActivity().getLayoutInflater().inflate(R.layout.bar, null);
            view.addView(barView);
            //View barView = view.inflate(getActivity(), R.layout.bar, view);
            View bar = barView.findViewById(R.id.bar);
            float height = view.getHeight() * TimeUtil.getDayPercent(s.getLength());
            if (height < minHeight) height = minHeight;

            ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.height = (int)height;
            bar.setLayoutParams(params);

            GradientDrawable bg = (GradientDrawable) bar.getBackground();
            bg.setColor(s.getTimer().getColor());

            float y = view.getHeight() * TimeUtil.getDayProgressPercent(s.getStartedAt());
            if (y > (view.getHeight() - height)) y = view.getHeight() - height;

            barView.setY(y);
        }
    }
}
