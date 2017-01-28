package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import therustyknife.timer.R;
import therustyknife.timer.Util;


public class CalendarContainerFragment extends Fragment{
    private CalendarFragment contentFragment;


    public CalendarContainerFragment(){}

    public static CalendarContainerFragment newInstance() {
        return new CalendarContainerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_container, container, false);

        contentFragment = QuickCalendarFragment.newInstance();
        setContentFragment();

        return view;
    }


    private void removeContentFragment(){
        if (contentFragment != null) getChildFragmentManager().beginTransaction().remove(contentFragment).commit();
    }

    private void setContentFragment(){
        if (contentFragment != null) getChildFragmentManager().beginTransaction().add(R.id.calendar_content, contentFragment).commit();
    }

    private void swapContentFragment(CalendarFragment f){
        removeContentFragment();
        contentFragment = f;
        setContentFragment();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (contentFragment != null) contentFragment.resume();
    }
}
