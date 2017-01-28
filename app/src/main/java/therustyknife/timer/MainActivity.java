package therustyknife.timer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ListView timerList;

    private SharedPreferences prefs;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences(Util.PREFS_KEY, 0);
        Util.timeFormatIndex = prefs.getInt(Util.TIME_FORMAT_PREF_KEY, 0);

        Util.context = getApplicationContext();

        Log.d(Util.TAG, "starting application...");

        Timer.loadList(getApplicationContext());

        // The add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        if (name.equals("")) name = getText(R.string.default_timer_name).toString();
                        Timer t = new Timer(name);
                        Timer.getList().add(t);
                        notifyDataChanged();
                        switchTo(t);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.ask_timer_name_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                alertDialog.show();
            }
        });

        timerList = (ListView) findViewById(R.id.timer_list);
        timerList.setAdapter(new TimerAdapter(this, Timer.getList()));
        timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switchTo((Timer)timerList.getAdapter().getItem(i));
            }
        });
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();

        Timer.saveList(getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();
        notifyDataChanged();
    }

    public void switchTo(Timer t){
        Intent intent = new Intent(this, TimerActivity.class);
        Util.getDataHolder().setData(t);
        startActivity(intent);
    }

    public void notifyDataChanged(){
        ((TimerAdapter)timerList.getAdapter()).notifyDataSetChanged();
    }
}
