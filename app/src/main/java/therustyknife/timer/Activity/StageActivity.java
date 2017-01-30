package therustyknife.timer.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import therustyknife.timer.R;
import therustyknife.timer.Timer;
import therustyknife.timer.TimerStage;
import therustyknife.timer.Adapters.TimerStageAdapter;
import therustyknife.timer.Util;


// the activity where the user can edit a timers stages
// contains the stage list
public class StageActivity extends AppCompatActivity {
    // an instance of this activity for abuse by the StageAdapter class // some better solution would be cool
    private static StageActivity current;
    public static StageActivity getCurrent(){ return current; }


    private ListView stageList;

    // the current timer and a convenience field for accessing the stage list
    private Timer timer;
    private ArrayList<TimerStage> stages;

    // the toolbar for setting timer color
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the toolbar, save it for later color changes
        setContentView(R.layout.activity_stage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the hacky static field
        current = this;

        // retrieve the passed-in data from our dataholder class
        Object t_data = Util.getDataHolder().getData();
        if (t_data == null || !(t_data instanceof Timer)){ // invalid data was passed so - return
            finish();
            return;
        }
        // set the data in data holder back to null
        Util.getDataHolder().setData(null);
        // populate the fields from data
        timer = (Timer) t_data;
        stages = timer.getStages();

        // set color and title of the toolbar
        setColor();
        setTitle(timer.getName() + getText(R.string.edit_stages));

        // set up the add stage button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ addNewStage(); }
        });

        // set up the stage list
        stageList = (ListView) findViewById(R.id.stage_list);
        stageList.setAdapter(new TimerStageAdapter(this, stages));

        // prompt the user to add a new stage if none are present
        if (timer.getStageCount() == 0) addNewStage();
    }


    @Override
    protected void onPause(){
        super.onPause();

        //save the timer to make sure it's up-to-date
        timer.save(getApplicationContext());
    }


    // asks for a stage name and creates a new stage if the user pressed ok
    private void addNewStage(){
        Util.showTextQuery(
            StageActivity.this,
            getString(R.string.ask_stage_name_title),
            getString(R.string.ask_stage_name_description),
            getString(R.string.default_stage_name),
            getString(R.string.ask_stage_name_ok),
            new Util.OnTextEnteredListener() {
                @Override
                public void onTextEntered(String text) {
                    // use default if entered text is empty
                    if (text.equals("")) text = getString(R.string.default_stage_name);

                    // create the new stage
                    TimerStage t = new TimerStage(text);
                    timer.addStage(t);

                    // update the list
                    ((TimerStageAdapter)stageList.getAdapter()).notifyDataSetChanged();
                }
        });
    }

    // changes the color of all appropriate views to the timers color
    private void setColor(){
        int col = timer.getColor();

        toolbar.setBackgroundColor(col);
    }
}
