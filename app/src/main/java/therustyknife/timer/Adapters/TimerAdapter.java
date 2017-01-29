package therustyknife.timer.Adapters;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import therustyknife.timer.R;
import therustyknife.timer.Timer;
import therustyknife.timer.Util;


public class TimerAdapter extends ArrayAdapter<Timer> {
    public TimerAdapter(Context context, ArrayList<Timer> timers){ super(context, R.layout.timer_list_item, timers); }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timer timer = getItem(position);

        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.timer_list_item, parent, false);

        TextView tvname = (TextView) convertView.findViewById(R.id.timer_list_item_name);
        TextView tvdetails = (TextView) convertView.findViewById(R.id.timer_list_item_details);
        TextView tvcompleted = (TextView) convertView.findViewById(R.id.timer_list_item_completed);

        convertView.findViewById(R.id.color_display).setBackgroundColor(timer.getColor());

        tvname.setText(timer.getName());
        tvdetails.setText(getContext().getText(R.string.total_length) + "" + Util.formatTime(timer.getTotalLength()) + getContext().getText(R.string.stage_length_separator) + timer.getStageCount() + getContext().getText(R.string.stage_count));
        tvcompleted.setText(Util.formatTime(timer.getStats().getRunTime()));

        return convertView;
    }
}
