package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import therustyknife.timer.Activity.TimerActivity;
import therustyknife.timer.R;
import therustyknife.timer.TimerState;
import therustyknife.timer.Util;


// the fragment that's shown when a timer is paused by the user
// contains the resume button, the previous and next buttons and the timer remaining to finish the current stage
public class TimerPausedFragment extends TimerFragment {
    // make a new instance of this fragment with the state and activity that got passed in
    public static TimerPausedFragment newInstance(TimerActivity activity, TimerState state) {
        argState = state;
        return (TimerPausedFragment) new TimerPausedFragment().setActivity(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_paused, container, false);

        // set the remaining time text
        ((TextView) view.findViewById(R.id.time_remaining)).setText(state.getRemainingTime());

        // set the button onClick listeners
        view.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ activity.prevTimer(); }
        });
        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ activity.resumeTimer(); }
        });
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ activity.skipTimer(); }
        });

        return view;
    }
}
