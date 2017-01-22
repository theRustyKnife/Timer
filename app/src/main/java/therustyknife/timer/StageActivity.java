package therustyknife.timer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
                AlertDialog alertDialog = new AlertDialog.Builder(StageActivity.this).create();
                alertDialog.setTitle(getString(R.string.ask_stage_name_title));

                // Textfield for name
                final View v = getLayoutInflater().inflate(R.layout.text_query_layout, null);
                TextView label = (TextView) v.findViewById(R.id.text_query_label);
                final EditText text = (EditText) v.findViewById(R.id.text_query_text);
                label.setText(getText(R.string.ask_stage_name_description));
                text.setHint(getText(R.string.default_stage_name));

                alertDialog.setView(v);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.ask_stage_name_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString();
                        if (name.equals("")) name = getText(R.string.default_stage_name).toString();
                        TimerStage t = new TimerStage(name);
                        stages.add(t);
                        ((TimerStageAdapter)stageList.getAdapter()).notifyDataSetChanged();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.ask_timer_name_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        stageList = (ListView) findViewById(R.id.stage_list);
        stageList.setAdapter(new TimerStageAdapter(getApplicationContext(), stages));
    }
}
