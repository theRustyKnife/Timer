package therustyknife.timer.Fragments;


import android.support.v4.app.Fragment;

import therustyknife.timer.Activity.TimerActivity;
import therustyknife.timer.TimerState;


// The base class for all the timer content fragments
public abstract class TimerFragment extends Fragment{
    // a hacky way to pass in the state (again)
    protected static TimerState argState;
    protected TimerState state;

    // the current activity for use by the fragments
    protected TimerActivity activity;


    public TimerFragment() {
        // populate the fields
        this.state = argState;
        argState = null;
    }


    // set the current activity
    protected TimerFragment setActivity(TimerActivity activity){
        this.activity = activity;
        return this;
    }

    // this gets called by the update loop and is intended for updating the display values
    public void update(){}
}
