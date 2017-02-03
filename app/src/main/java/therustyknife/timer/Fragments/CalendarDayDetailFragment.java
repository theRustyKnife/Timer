package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import therustyknife.timer.R;
import therustyknife.timer.Util;


//TODO: comments!
public class CalendarDayDetailFragment extends CalendarFragment {
    private CalendarDaySmallFragment timeline;

    private int dayOffset;


    public static CalendarDayDetailFragment newInstance(int dayOffset) {
        CalendarDayDetailFragment res = new CalendarDayDetailFragment();

        res.setDayOffset(dayOffset);

        return res;
    }
    public static CalendarDayDetailFragment newInstance(){ return newInstance(0); }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day_detail, container, false);

        timeline = CalendarDaySmallFragment.newInstance(dayOffset, (int)getResources().getDimension(R.dimen.day_detail_timeline_height), false);
        getChildFragmentManager().beginTransaction().add(R.id.timeline_container, timeline).commit();

        //TODO: add names and times and stuff...

        return view;
    }


    private void setDayOffset(int dayOffset){ this.dayOffset = dayOffset; }
}
