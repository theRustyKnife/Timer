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


public class CalendarBigFragment extends CalendarFragment {
    private static int DAY_DISPLAY_NUMBER = 7;

    public CalendarBigFragment(){}

    public static CalendarBigFragment newInstance() {
        return new CalendarBigFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_big, container, false);

        int today = -DateTime.now().dayOfWeek().get();
        for (int i = 0; i < DAY_DISPLAY_NUMBER; i++)
            getChildFragmentManager().beginTransaction().add(R.id.main_content, CalendarDaySmallFragment.newInstance(++today)).commit();

        return view;
    }
}
