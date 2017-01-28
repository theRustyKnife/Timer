package therustyknife.timer.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import therustyknife.timer.Fragments.CalendarContainerFragment;
import therustyknife.timer.R;
import therustyknife.timer.Timer;
import therustyknife.timer.Adapters.TimerAdapter;
import therustyknife.timer.Util;

public class MainActivity extends AppCompatActivity {
    private CalendarContainerFragment calendar;

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
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.showTextQuery(
                        MainActivity.this,
                        getString(R.string.ask_timer_name_title),
                        getString(R.string.ask_timer_name_description),
                        getString(R.string.default_timer_name),
                        getString(R.string.ask_timer_name_ok),
                        new Util.OnTextEnteredListener() {
                            @Override
                            public void onTextEntered(String text) {
                                if (text.equals("")) text = getString(R.string.default_timer_name);
                                Timer t = new Timer(text);
                                Timer.getList().add(t);
                                notifyDataChanged();
                                switchTo(t);
                            }
                });
            }
        });

        calendar = CalendarContainerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_container_frame, calendar).commit();

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
