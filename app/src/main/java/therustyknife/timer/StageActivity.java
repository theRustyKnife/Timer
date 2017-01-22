package therustyknife.timer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class StageActivity extends AppCompatActivity {
    private ListView stageList;

    private Timer timer;
    private ArrayList<TimerStage> stages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Object t_data = Util.getDataHolder().getData();
        if (t_data == null || !(t_data instanceof Timer)){
            Toast.makeText(getApplicationContext(), "An error has occured while loading a timer. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Util.getDataHolder().setData(null);
        timer = (Timer) t_data;
        stages = timer.getStages();

        setTitle(timer.getName() + getText(R.string.edit_stages));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implement adding new stages
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        stageList = (ListView) findViewById(R.id.stage_list);
        stageList.setAdapter(new TimerStageAdapter(getApplicationContext(), stages));
    }

}
