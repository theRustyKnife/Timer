package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import therustyknife.timer.R;


// TODO: finish
public class CalendarDayDetailFragment extends CalendarFragment {
    public static CalendarDayDetailFragment newInstance() { return new CalendarDayDetailFragment(); }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_big, container, false);

        //TODO: init code here

        return view;
    }
}
