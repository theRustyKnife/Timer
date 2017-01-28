package therustyknife.timer.Fragments;

import android.support.v4.app.Fragment;

import therustyknife.timer.Activity.TimerActivity;
import therustyknife.timer.TimerState;


public abstract class TimerFragment extends Fragment{
    protected static TimerState argState;

    protected TimerActivity activity;
    protected TimerState state;


    public TimerFragment() {
        this.state = argState;
        argState = null;
    }

    protected TimerFragment setActivity(TimerActivity activity){
        this.activity = activity;
        return this;
    }

    public void update(){}
}
