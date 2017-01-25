package therustyknife.timer;

import android.os.Bundle;
import android.os.health.TimerStat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class TimerPausedFragment extends TimerFragment {
    private TextView timeRemaining;


    public static TimerPausedFragment newInstance(TimerActivity activity, TimerState state) {
        argState = state;
        return (TimerPausedFragment) new TimerPausedFragment().setActivity(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_paused, container, false);

        timeRemaining = (TextView) view.findViewById(R.id.time_remaining);

        ((ImageButton) view.findViewById(R.id.prev)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Util.TAG, "clicked prev");
                activity.prevTimer();
            }
        });
        ((ImageButton) view.findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Util.TAG, "clicked play");
                activity.resumeTimer();
            }
        });
        ((ImageButton) view.findViewById(R.id.next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Util.TAG, "clicked next");
                activity.skipTimer();
            }
        });

        update();

        return view;
    }

    @Override
    public void update(){
        if (state != null && timeRemaining != null) timeRemaining.setText(state.getRemainingTime());
    }
}
