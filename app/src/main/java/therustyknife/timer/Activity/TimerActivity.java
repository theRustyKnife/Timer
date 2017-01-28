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

public class TimerActivity extends AppCompatActivity{
    private Timer timer;
    private TimerState state;

    private TextView statusDisplay;

    private LinearLayout nextFrame;
    private TextView nextName;
    private TextView nextTime;

    private FrameLayout content;
    private TimerFragment contentFragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the passed in timer, return if none was passed
        Object t_data = Util.getDataHolder().getData();
        if (timer == null && (t_data == null || !(t_data instanceof Timer))){
            Log.d(Util.TAG, "No timer in data");
            Toast.makeText(getApplicationContext(), "An error has occured while loading a timer. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (timer == null) {
            Util.getDataHolder().setData(null);
            timer = (Timer) t_data;
            state = timer.getState();
        }

        setColor();

        timer.initState();

        content = (FrameLayout) findViewById(R.id.main_content);

        statusDisplay = (TextView) findViewById(R.id.timer_status);

        View.OnClickListener addStageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer.getStageCount() == 0) switchToStages();
            }
        };
        statusDisplay.setOnClickListener(addStageListener);
        content.setOnClickListener(addStageListener);

        nextFrame = (LinearLayout) findViewById(R.id.timer_next_frame);
        nextName = (TextView) findViewById(R.id.timer_next_stage_name);
        nextTime = (TextView) findViewById(R.id.timer_next_stage_time);

        if (timer.getStageCount() != 0) {
            contentFragment = TimerStoppedFragment.newInstance(this, state);
            setContentFragment();
        }

        updateDisplay();

        setTitle(timer.getName());

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_delete:
                Util.showConfirmBox(this, getString(R.string.delete_title), getString(R.string.yes), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        timer.delete();
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                return true;
            case R.id.action_edit_stages:
                if (state.isStopped()) switchToStages();
                else Toast.makeText(getApplicationContext(), R.string.stop_first, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_change_color:
                int color = timer.getColor();

                //TODO: implement material palette selection
                final ColorPicker cp = new ColorPicker(TimerActivity.this, Color.red(color), Color.green(color), Color.blue(color));
                cp.setOnColorSelected(new OnColorSelected() {
                    @Override
                    public void returnColor(int col) {
                        timer.setColor(col);
                        setColor();
                        cp.dismiss();
                    }
                });
                cp.show();

                return true;
            case R.id.action_rename:
                Util.showTextQuery(
                        TimerActivity.this,
                        getString(R.string.timer_rename),
                        getString(R.string.ask_timer_name_description),
                        timer.getName(),
                        new Util.OnTextEnteredListener() {
                            @Override
                            public void onTextEntered(String text) {
                                timer.setName(text);
                                toolbar.setTitle(text);
                            }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void switchToStages(){
        Intent intent = new Intent(this, StageActivity.class);
        Util.getDataHolder().setData(timer);
        startActivity(intent);
    }


    @Override
    protected void onPause(){
        super.onPause();
        Timer.saveList(getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(Util.TAG, "timer stage count = " + timer.getStageCount());

        if (timer.getStageCount() == 0) {
            if (contentFragment != null) removeContentFragment();
        }else if (state.isStopped()){
            removeContentFragment();
            contentFragment = TimerStoppedFragment.newInstance(this, state);
            setContentFragment();
        }

        for (TimerFragment f : removeOnResume) getSupportFragmentManager().beginTransaction().remove(f).commit();

        if (setOnResume != null){
            contentFragment = setOnResume;
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, setOnResume).commit();
        }

        updateDisplay();
    }

    @Override
    public void onBackPressed(){
        if (state != null && !state.isStopped()) {
            Util.showConfirmBox(this, getString(R.string.exit_timer_title), getString(R.string.yes), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    state.stop(true);
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        else super.onBackPressed();
    }

    private void updateDisplay(){
        if (state == null) return;
        statusDisplay.setText(state.getStatus());

        TimerStage nextStage = state.getNext();
        if (nextStage != null){
            nextName.setText(nextStage.getName());
            nextTime.setText(Util.formatTime(nextStage.getTime()));
            nextFrame.setVisibility(View.VISIBLE);
        }
        else nextFrame.setVisibility(View.INVISIBLE);

        if (state.isStopped() && (contentFragment == null || !(contentFragment instanceof TimerStoppedFragment))) {
            Log.d(Util.TAG, "setting new fragment");
            swapContentFragment(TimerStoppedFragment.newInstance(this, state));
        }
    }

    private void update(){
        updateDisplay();
        if (contentFragment != null) contentFragment.update();
    }

    public void startTimer(){
        if (state.start()) swapContentFragment(TimerRunningFragment.newInstance(this, state));
        else{
            Log.d(Util.TAG, "Failed to start timer " + timer.getName());
            Toast.makeText(getApplicationContext(), R.string.failed_start, Toast.LENGTH_LONG).show();
        }
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

    private ArrayList<TimerFragment> removeOnResume = new ArrayList<TimerFragment>();
    private void removeContentFragment(){
        try{ if (contentFragment != null) getSupportFragmentManager().beginTransaction().remove(contentFragment).commit(); }
        catch(IllegalStateException e){ removeOnResume.add(contentFragment); }
    }

    private TimerFragment setOnResume = null;
    private void setContentFragment(){
        try{ if (contentFragment != null) getSupportFragmentManager().beginTransaction().add(R.id.main_content, contentFragment).commit(); }
        catch(IllegalStateException e){ setOnResume = contentFragment; }
    }

    private void swapContentFragment(TimerFragment f){
        removeContentFragment();
        contentFragment = f;
        setContentFragment();
    }

    private void setColor(){
        int col = timer.getColor();
        toolbar.setBackgroundColor(col);
    }
}
