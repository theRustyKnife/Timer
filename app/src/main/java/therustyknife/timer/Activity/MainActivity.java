package therustyknife.timer.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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


// the main activity that the users sees when she opens the app
// contains the timer list and the calendar
public class MainActivity extends AppCompatActivity {
    private CalendarContainerFragment calendar;

    private ListView timerList;

    private SharedPreferences prefs;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up the toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the user preferences and set up the globals according to that
        prefs = getSharedPreferences(Util.PREFS_KEY, 0);
        Util.timeFormatIndex = prefs.getInt(Util.TIME_FORMAT_PREF_KEY, 0);

        Util.context = getApplicationContext();

        // load the timer list from storage
        Timer.loadList();

        // set up the add button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the add timer dialog
                Util.showTextQuery(
                        MainActivity.this,
                        getString(R.string.ask_timer_name_title),
                        getString(R.string.ask_timer_name_description),
                        getString(R.string.default_timer_name),
                        getString(R.string.ask_timer_name_ok),
                        new Util.OnTextEnteredListener() {
                            @Override
                            public void onTextEntered(String text) {
                                // use default if entered text is empty
                                if (text.equals("")) text = getString(R.string.default_timer_name);

                                // create the new timer
                                Timer t = new Timer(text);
                                Timer.getList().add(t);

                                // update the listview data set and switch to the new timer
                                notifyDataChanged();
                                switchTo(t);
                            }
                });
            }
        });

        // remove the calendar fragment if it exists to prevent duplication
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.calendar_container_frame);
        if (f != null) getSupportFragmentManager().beginTransaction().remove(f).commit();

        // add the calendar fragment
        calendar = CalendarContainerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_container_frame, calendar).commit();

        // set up the timer listview
        timerList = (ListView) findViewById(R.id.timer_list);
        timerList.setAdapter(new TimerAdapter(this, Timer.getList()));
        timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // switch to the timer that got clicked
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
        // Handle action bar item clicks here.
        int id = item.getItemId();

        // handle individual menu item clicks // replace this with a switch if more items get added
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

        // make sure an up-to-date version of the timer list is saved
        Timer.saveList();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // update the timer list
        notifyDataChanged();
    }


    // switches to the timer activity with the passed-in timer
    public void switchTo(Timer t){
        Intent intent = new Intent(this, TimerActivity.class);
        Util.getDataHolder().setData(t); // put the timer in our data holder class as we can't pass it to the activity directly
        startActivity(intent);
    }

    // updates the timer list
    public void notifyDataChanged(){ ((TimerAdapter)timerList.getAdapter()).notifyDataSetChanged(); }
}
