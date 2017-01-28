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

public class StageActivity extends AppCompatActivity {
    private static StageActivity current;
    public static StageActivity getCurrent(){ return current; }


    private ListView stageList;

    private Timer timer;
    private ArrayList<TimerStage> stages;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        current = this;

        Object t_data = Util.getDataHolder().getData();
        if (t_data == null || !(t_data instanceof Timer)){
            Toast.makeText(getApplicationContext(), "An error has occured while loading a timer. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Util.getDataHolder().setData(null);
        timer = (Timer) t_data;
        stages = timer.getStages();

        setColor();
        setTitle(timer.getName() + getText(R.string.edit_stages));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){ addNewStage(); }
        });

        stageList = (ListView) findViewById(R.id.stage_list);
        stageList.setAdapter(new TimerStageAdapter(this, stages));

        if (timer.getStageCount() == 0) addNewStage();
    }

    @Override
    protected void onPause(){
        super.onPause();
        timer.save(getApplicationContext());
    }

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
                    if (text.equals("")) text = getString(R.string.default_stage_name);
                    TimerStage t = new TimerStage(text);
                    timer.addStage(t);
                    ((TimerStageAdapter)stageList.getAdapter()).notifyDataSetChanged();
                }
        });
    }

    private void setColor(){
        int col = timer.getColor();
        toolbar.setBackgroundColor(col);
    }
}
