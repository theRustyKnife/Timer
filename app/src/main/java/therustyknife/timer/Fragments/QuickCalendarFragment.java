package therustyknife.timer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import therustyknife.timer.R;
import therustyknife.timer.TimeUtil;
import therustyknife.timer.Timer;
import therustyknife.timer.TimerStats;
import therustyknife.timer.Util;


// the quick week overview display
// contains the day names with times spent in timers that day
//TODO: copypasta needs to go
public class QuickCalendarFragment extends CalendarFragment {
    // references to the value TextViews
    private TextView tvmon;
    private TextView tvtue;
    private TextView tvwed;
    private TextView tvthu;
    private TextView tvfri;
    private TextView tvsat;
    private TextView tvsun;


    public static QuickCalendarFragment newInstance() {
        return new QuickCalendarFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_calendar, container, false);

        // get the references to our value views
        tvmon = (TextView) view.findViewById(R.id.quick_calendar_mon);
        tvtue = (TextView) view.findViewById(R.id.quick_calendar_tue);
        tvwed = (TextView) view.findViewById(R.id.quick_calendar_wed);
        tvthu = (TextView) view.findViewById(R.id.quick_calendar_thu);
        tvfri = (TextView) view.findViewById(R.id.quick_calendar_fri);
        tvsat = (TextView) view.findViewById(R.id.quick_calendar_sat);
        tvsun = (TextView) view.findViewById(R.id.quick_calendar_sun);

        updateValues();

        return view;
    }


    // set the value for the specified day into the specified TextView
    private void updateValue(TextView tv, int dayOffset){
        if (dayOffset > 0){ // set "--" if the day hasn't yet happened
            tv.setText("--");
            return;
        }

        // sum up the time spent in timers and set that value to the TextView
        int res = 0;
        for (Timer t : Timer.getList()){
            res += t.getStats().getRunInDay(dayOffset);
        }
        tv.setText(Util.formatTime(res));
    }

    // set values into the TextViews
    public void updateValues(){
        int today = -DateTime.now().dayOfWeek().get();

        updateValue(tvmon, ++today);
        updateValue(tvtue, ++today);
        updateValue(tvwed, ++today);
        updateValue(tvthu, ++today);
        updateValue(tvfri, ++today);
        updateValue(tvsat, ++today);
        updateValue(tvsun, ++today);
    }

    @Override
    public void resume(){
        updateValues();
    } // should never be run
}
