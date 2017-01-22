package therustyknife.timer;


import android.content.Context;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class TimerStageAdapter extends ArrayAdapter<TimerStage> {
    public TimerStageAdapter(Context context, ArrayList<TimerStage> stages){ super(context, R.layout.stage_list_item, stages); }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TimerStage stage = getItem(position);

        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.stage_list_item, parent, false);

        TextView number = (TextView) convertView.findViewById(R.id.stage_list_item_number);
        EditText name = (EditText) convertView.findViewById(R.id.stage_list_item_name);

        EditText pauseBefore = (EditText) convertView.findViewById(R.id.stage_list_item_pause_before);
        EditText duration = (EditText) convertView.findViewById(R.id.stage_list_item_duration);
        EditText pauseAfter = (EditText) convertView.findViewById(R.id.stage_list_item_pause_after);

        number.setText(parent.getContext().getText(R.string.stage_n).toString() + position);

        name.setText(stage.getName());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                stage.setName(editable.toString());
            }
        });

        //TODO: implement setting the rest of the changed values, make some time pickers, add delete button onclick

        pauseBefore.setText(stage.getPauseBefore() + "");
        duration.setText(stage.getTime() + "");
        pauseAfter.setText(stage.getPauseAfter() + "");

        return convertView;
    }
}
