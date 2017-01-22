package therustyknife.timer;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class TimerStageAdapter extends ArrayAdapter<TimerStage> {
    public TimerStageAdapter(Context context, ArrayList<TimerStage> stages){ super(context, R.layout.stage_list_item, stages); }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimerStage stage = getItem(position);

        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.stage_list_item, parent, false);

        //TODO: implement data bindings

        return convertView;
    }
}
