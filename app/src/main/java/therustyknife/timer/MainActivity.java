package therustyknife.timer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Timer> timers;

    private ListView timerList;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AlertDialog alert = new AlertDialog.Builder(getApplicationContext()).create();
                alert.setTitle(getString(R.string.ask_timer_name_title));

                // Textfield for name
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint(R.string.name_hint);
                alert.setView(input);

                alert.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.ask_timer_name_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: create a new Timer
                    }
                });

                alert.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.ask_timer_name_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();*/
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(getString(R.string.ask_timer_name_title));

                // Textfield for name
                final View v = getLayoutInflater().inflate(R.layout.text_query_layout, null);
                TextView label = (TextView) v.findViewById(R.id.text_query_label);
                final EditText text = (EditText) v.findViewById(R.id.text_query_text);
                label.setText(getText(R.string.ask_timer_name_description));
                text.setHint(getText(R.string.default_timer_name));

                alertDialog.setView(v);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.ask_timer_name_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString();
                        Timer t = new Timer(name);
                        timers.add(t);
                        switchTo(t);
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

        timers = new ArrayList<Timer>();
        //TODO: remove test values
        timers.add(new Timer("Timer 1"));
        timers.add(new Timer("Timer 2"));

        Timer t = new Timer("Timer 3");
        t.addStage(new TimerStage("Stage 1", 65, 10));
        t.addStage(new TimerStage("Stage 2", 20, 2, 13));

        timers.add(t);

        for(int i = 3; i <= 10; i++) timers.add(new Timer("Timer " + i));

        timerList = (ListView) findViewById(R.id.timer_list);
        timerList.setAdapter(new TimerAdapter(getApplicationContext(), timers));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchTo(Timer t){
        Intent intent = new Intent(this, TimerActivity.class);
        Util.getDataHolder().setData(t);
        startActivity(intent);
    }
}
