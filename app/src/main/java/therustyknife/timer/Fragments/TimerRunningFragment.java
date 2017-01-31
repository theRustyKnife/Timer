package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import therustyknife.timer.Activity.TimerActivity;
import therustyknife.timer.R;
import therustyknife.timer.TimerState;


// the fragment that's shown when the timer is running
// contains just the time countdown
public class TimerRunningFragment extends TimerFragment {
    // the TextView for the countdown
    private TextView time;


    // make a new instance of this fragment with the state and activity that got passed in
    public static TimerRunningFragment newInstance(TimerActivity activity, TimerState state) {
        argState = state;
        return (TimerRunningFragment) new TimerRunningFragment().setActivity(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_running, container, false);

        // get the reference to the countdown view
        time = (TextView) view.findViewById(R.id.time_remaining);

        // make clicking anywhere pause the timer
        view.findViewById(R.id.running_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ activity.pauseTimer(); }
        });

        update();

        return view;
    }

    @Override
    public void update(){
        if (state != null && time != null) time.setText(state.getRemainingTime());
    }
}
