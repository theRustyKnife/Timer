package therustyknife.timer;


import android.content.Context;

import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;


public class TimerStageAdapter extends ArrayAdapter<TimerStage> {
    private ArrayList<TimerStage> stages;

    public TimerStageAdapter(Context context, ArrayList<TimerStage> stages){
        super(context, R.layout.stage_list_item, stages);
        this.stages = stages;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TimerStage stage = getItem(position);

        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.stage_list_item, parent, false);

        // delete button
        Button delete = (Button) convertView.findViewById(R.id.stage_list_item_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(StageActivity.getCurrent()).create();
                alertDialog.setTitle(getContext().getString(R.string.delete_title));

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stages.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        //id - can't be edited luckily
        TextView number = (TextView) convertView.findViewById(R.id.stage_list_item_number);
        number.setText(parent.getContext().getText(R.string.stage_n).toString() + (position + 1));

        // name
        EditText name = (EditText) convertView.findViewById(R.id.stage_list_item_name);
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

        // all the times //TODO: change this to some other view type
        EditText pauseBefore = (EditText) convertView.findViewById(R.id.stage_list_item_pause_before);
        pauseBefore.setText(stage.getPauseBefore() + "");
        pauseBefore.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String t = editable.toString();
                if (!t.equals("")) stage.setPauseBefore(Integer.valueOf(t));
            }
        });

        EditText duration = (EditText) convertView.findViewById(R.id.stage_list_item_duration);
        duration.setText(stage.getTime() + "");
        duration.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String t = editable.toString();
                if (!t.equals("")) stage.setTime(Integer.valueOf(t));
            }
        });

        return convertView;
    }
}
