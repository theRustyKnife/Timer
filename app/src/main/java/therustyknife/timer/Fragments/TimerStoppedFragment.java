package therustyknife.timer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import therustyknife.timer.Activity.TimerActivity;
import therustyknife.timer.R;
import therustyknife.timer.TimerState;
import therustyknife.timer.Util;


public class TimerStoppedFragment extends TimerFragment {
    public static TimerStoppedFragment newInstance(TimerActivity activity, TimerState state) {
        argState = state;
        return (TimerStoppedFragment) new TimerStoppedFragment().setActivity(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_stopped, container, false);

        ((ImageButton) view.findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Util.TAG, "clicked play");
                activity.startTimer();
            }
        });

        return view;
    }
}
