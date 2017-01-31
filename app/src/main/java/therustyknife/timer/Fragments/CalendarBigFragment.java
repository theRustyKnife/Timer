package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import therustyknife.timer.R;
import therustyknife.timer.Timer;


// displays the overview of this week with timelines
public class CalendarBigFragment extends CalendarFragment {
    // determines the number of days that will be shown, for a week this should obviously be 7
    private static int DAY_DISPLAY_NUMBER = 7;

    // the listener to call when a day is clicked
    private OnDetailsRequestedListener onDetailsRequestedListener;


    public static CalendarBigFragment newInstance(OnDetailsRequestedListener onDetailsRequestedListener) {
        CalendarBigFragment res = new CalendarBigFragment();

        res.setOnDetailsRequestedListener(onDetailsRequestedListener);

        return res;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_big, container, false);

        // get today's day offset
        int today = -DateTime.now().dayOfWeek().get();
        // add the individual day fragments
        for (int i = 0; i < DAY_DISPLAY_NUMBER; i++) {
            CalendarDaySmallFragment f = CalendarDaySmallFragment.newInstance(++today);

            // set the listener for switching to the detail view
            f.setOnDayClickListener(new CalendarDaySmallFragment.OnDayClickListener() {
                @Override
                public void onClick(int dayOffset){ if (onDetailsRequestedListener != null) onDetailsRequestedListener.onDetailsRequested(dayOffset); }
            });

            getChildFragmentManager().beginTransaction().add(R.id.main_content, f).commit();
        }

        return view;
    }


    private void setOnDetailsRequestedListener(OnDetailsRequestedListener onDetailsRequestedListener){ this.onDetailsRequestedListener = onDetailsRequestedListener; }


    public interface OnDetailsRequestedListener{
        void onDetailsRequested(int dayOffset);
    }
}
