package therustyknife.timer.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.OnColorSelected;

import java.util.ArrayList;

import therustyknife.timer.R;
import therustyknife.timer.Timer;
import therustyknife.timer.Fragments.TimerFragment;
import therustyknife.timer.Fragments.TimerPausedFragment;
import therustyknife.timer.Fragments.TimerRunningFragment;
import therustyknife.timer.TimerStage;
import therustyknife.timer.TimerState;
import therustyknife.timer.Fragments.TimerStoppedFragment;
import therustyknife.timer.Util;


// the activity that serves the actual purpose of the app
// contains the countdown, other status displays, some options for editing the timer
public class TimerActivity extends AppCompatActivity{
    private Timer timer; // the timer associated with this activity
    private TimerState state; // convenience for accessing the stats

    private Toolbar toolbar;

    private TextView statusDisplay;

    private LinearLayout nextFrame;
    private TextView nextName;
    private TextView nextTime;

    // the container for the content and the current content fragment
    private FrameLayout content;
    private TimerFragment contentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the toolbar, save it for changing color in the future
        setContentView(R.layout.activity_timer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get references to our views
        content = (FrameLayout) findViewById(R.id.main_content);
        statusDisplay = (TextView) findViewById(R.id.timer_status);
        nextFrame = (LinearLayout) findViewById(R.id.timer_next_frame);
        nextName = (TextView) findViewById(R.id.timer_next_stage_name);
        nextTime = (TextView) findViewById(R.id.timer_next_stage_time);

        // get the passed in timer, return if none was passed
        Object t_data = Util.getDataHolder().getData(); // retrieve data from our holder class
        if (timer == null && (t_data == null || !(t_data instanceof Timer))){ // return if no timer was passed in and there was no timer from before
            finish();
            return;
        }
        // if we don't yet have a timer get it from the data
        if (timer == null) {
            Util.getDataHolder().setData(null); // set null back to the data holder
            timer = (Timer) t_data;
            state = timer.getState();
        }

        // set color and title to the toolbar
        setColor();
        setTitle(timer.getName());

        // make the timer create a new instance of TimerState to use for this session
        timer.initState();

        // add a new stage on click if there are none present
        View.OnClickListener addStageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer.getStageCount() == 0) switchToStages();
            }
        };
        statusDisplay.setOnClickListener(addStageListener);
        content.setOnClickListener(addStageListener);

        // if there are some stages, display the play button
        if (timer.getStageCount() != 0) {
            contentFragment = TimerStoppedFragment.newInstance(this, state);
            setContentFragment();
        }

        updateDisplay();

        // start the update loop
        final Handler h = new Handler();
        final int delay = 100; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                update();
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                // prompt the user if she's sure she wants to delete this timer
                Util.showConfirmBox(
                        this,
                        getString(R.string.delete_title),
                        getString(R.string.yes),
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timer.delete();
                                finish();
                            }
                        });
                return true;

            case R.id.action_edit_stages:
                if (state.isStopped()) switchToStages();
                else {
                    // ask the user if she wants to stop the timer and edit stages
                    Util.showConfirmBox(
                            this,
                            getString(R.string.edit_stage_warning),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    state.stop();
                                    switchToStages();
                                }
                            });
                }
                return true;

            case R.id.action_change_color:
                // get the current color
                int color = timer.getColor();

                //TODO: implement material palette selection
                // display the color picker
                final ColorPicker cp = new ColorPicker(TimerActivity.this, Color.red(color), Color.green(color), Color.blue(color));
                cp.setOnColorSelected(new OnColorSelected() {
                    @Override
                    public void returnColor(int col) {
                        // set the new color and dismiss
                        timer.setColor(col);
                        setColor();
                        cp.dismiss();
                    }
                });
                cp.show();
                return true;

            case R.id.action_rename:
                // ask the user to enter a new name
                Util.showTextQuery(
                        TimerActivity.this,
                        getString(R.string.timer_rename),
                        getString(R.string.ask_timer_name_description),
                        timer.getName(),
                        getString(R.string.ok),
                        timer.getName(),
                        new Util.OnTextEnteredListener() {
                            @Override
                            public void onTextEntered(String text) {
                                // set the name and title
                                timer.setName(text);
                                toolbar.setTitle(text);
                            }
                        });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();

        // save the timer list to make sure it's up-to-date
        Timer.saveList();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // update the content when returning from stage edit
        if (timer.getStageCount() == 0) // if there are no stages remove the content
            if (contentFragment != null) removeContentFragment();
        else if (state.isStopped()){ // otherwise make sure there's the stopped fragment (and only that) if the timer is stopped
            removeContentFragment();
            contentFragment = TimerStoppedFragment.newInstance(this, state);
            setContentFragment();
        }

        // remove any fragments that may have been left over from the timer running in background
        for (TimerFragment f : removeOnResume) getSupportFragmentManager().beginTransaction().remove(f).commit();

        // set the fragment that was supposed to be set when away, don't do this if the timer is stopped to prevent duplication // some better solution would be nice here but it works
        if (setOnResume != null && !state.isStopped()){
            contentFragment = setOnResume;
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, setOnResume).commit();
        }

        // reset the temp fragment variables
        removeOnResume = new ArrayList<>();
        setOnResume = null;

        updateDisplay();
    }

    @Override
    public void onBackPressed(){
        if (state != null && !state.isStopped()) {
            // if the timer is running ask the user if she wants to stop it when exiting
            Util.showConfirmBox(
                    this,
                    getString(R.string.exit_timer_title),
                    getString(R.string.yes),
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // stop the timer and exit
                            state.stop(true);
                            finish();
                        }
                    });
        }
        else super.onBackPressed();
    }


    // switch to the stage activity
    protected void switchToStages(){
        Intent intent = new Intent(this, StageActivity.class);
        Util.getDataHolder().setData(timer); // put the timer into our data holder
        startActivity(intent);
    }

    // update all the views from the current state of the timer
    private void updateDisplay(){
        if (state == null) return; // make sure there's data to update from
        statusDisplay.setText(state.getStatus());

        // update the next stage display based on the next stage of the timer // make it invisible when there's no next stage
        TimerStage nextStage = state.getNext();
        if (nextStage != null){
            nextName.setText(nextStage.getName());
            nextTime.setText(Util.formatTime(nextStage.getTime()));
            nextFrame.setVisibility(View.VISIBLE);
        }
        else nextFrame.setVisibility(View.INVISIBLE);

        // make sure the fragment gets correctly replaced when the timer is stopped
        if (state.isStopped() && (contentFragment == null || !(contentFragment instanceof TimerStoppedFragment))) {
            swapContentFragment(TimerStoppedFragment.newInstance(this, state));
        }
    }

    // set the timers color to all appropriate views
    private void setColor(){
        int col = timer.getColor();

        toolbar.setBackgroundColor(col);
    }

    // the update callback method
    private void update(){
        updateDisplay();
        if (contentFragment != null) contentFragment.update(); // update the content fragments inner state
    }

    // starts the timer, displays a toast message to the user if failed
    public void startTimer(){
        if (state.start()) swapContentFragment(TimerRunningFragment.newInstance(this, state));
        else Toast.makeText(getApplicationContext(), R.string.failed_start, Toast.LENGTH_LONG).show();
    }

    public void pauseTimer(){
        swapContentFragment(TimerPausedFragment.newInstance(this, state));
        state.pause();
    }

    public void resumeTimer(){
        swapContentFragment(TimerRunningFragment.newInstance(this, state));
        state.resume();
    }

    public void skipTimer(){
        swapContentFragment(TimerRunningFragment.newInstance(this, state));
        state.skip();
    }

    public void prevTimer(){
        swapContentFragment(TimerRunningFragment.newInstance(this, state));
        state.prev();
    }


    // remove the current content fragment, put it into a backlog list to be removed on resume if failed
    private ArrayList<TimerFragment> removeOnResume = new ArrayList<>(); // the failed-to-remove backlog list
    private void removeContentFragment(){
        try{ if (contentFragment != null) getSupportFragmentManager().beginTransaction().remove(contentFragment).commit(); }
        catch(IllegalStateException e){ removeOnResume.add(contentFragment); }
    }

    // set a new content fragment, let it be retried on resume if failed
    private TimerFragment setOnResume = null; // the fragment to be set on resume
    private void setContentFragment(){
        try{ if (contentFragment != null) getSupportFragmentManager().beginTransaction().add(R.id.main_content, contentFragment).commit(); }
        catch(IllegalStateException e){ setOnResume = contentFragment; }
    }

    // swap the current content fragment for a new one using the above methods
    private void swapContentFragment(TimerFragment f){
        removeContentFragment();
        contentFragment = f;
        setContentFragment();
    }
}
