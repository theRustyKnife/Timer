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


public class TimerRunningFragment extends TimerFragment {
    private TextView time;

    public static TimerRunningFragment newInstance(TimerActivity activity, TimerState state) {
        argState = state;
        return (TimerRunningFragment) new TimerRunningFragment().setActivity(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_running, container, false);

        time = (TextView) view.findViewById(R.id.time_remaining);

        ((LinearLayout) view.findViewById(R.id.running_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.pauseTimer();
            }
        });

        update();

        return view;
    }

    @Override
    public void update(){
        if (state != null && time != null) time.setText(state.getRemainingTime());
    }
}
