package therustyknife.timer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
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

        setTitle(timer.getName());
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
                timer.delete();
                finish();
                return true;
            case R.id.action_edit_stages:
                Intent intent = new Intent(this, StageActivity.class);
                Util.getDataHolder().setData(timer);
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
}
