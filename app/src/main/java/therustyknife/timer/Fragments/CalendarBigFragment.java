package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import therustyknife.timer.R;


public class CalendarBigFragment extends CalendarFragment {
    public CalendarBigFragment(){}

    public static CalendarBigFragment newInstance() {
        return new CalendarBigFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_big, container, false);

        //TODO: set the values here

        return view;
    }
}
