package therustyknife.timer.Adapters;


import android.app.Activity;
import android.content.Context;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;

import java.util.ArrayList;

import therustyknife.timer.Activity.StageActivity;
import therustyknife.timer.R;
import therustyknife.timer.TimerStage;
import therustyknife.timer.Util;


// an adapter for the stage list, handles stage editing
public class TimerStageAdapter extends ArrayAdapter<TimerStage> {
    private ArrayList<TimerStage> stages; // for stage removal
    private Activity activity;


    public TimerStageAdapter(Activity activity, ArrayList<TimerStage> stages){
        super(activity, R.layout.stage_list_item, stages);
        this.stages = stages;
        this.activity = activity;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get the stage in question
        final TimerStage stage = getItem(position);

        // always create new view to prevent data loss or duplication and other problems like that
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.stage_list_item, parent, false);

        // get references to our views
        Button delete = (Button) convertView.findViewById(R.id.stage_list_item_delete);
        TextView number = (TextView) convertView.findViewById(R.id.stage_list_item_number);
        final TextView name = (TextView) convertView.findViewById(R.id.stage_list_item_name);
        final TextView pauseBefore = (TextView) convertView.findViewById(R.id.stage_list_item_pause_before);
        final TextView duration = (TextView) convertView.findViewById(R.id.stage_list_item_duration);

        // set up the delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ask the user if she's sure she wants to delete the stage
                Util.showConfirmBox(
                        activity,
                        getContext().getString(R.string.delete_title),
                        getContext().getString(R.string.yes),
                        getContext().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stages.remove(position);
                                notifyDataSetChanged();
                            }
                        });
            }
        });

        // id - can't be edited luckily
        number.setText(parent.getContext().getText(R.string.stage_n).toString() + (position + 1));

        // name
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ask the user to enter a new name for the stage
                Util.showTextQuery(
                        activity,
                        getContext().getString(R.string.stage_change_name),
                        getContext().getString(R.string.ask_stage_name_description),
                        stage.getName(),
                        getContext().getString(R.string.ok),
                        stage.getName(),
                        new Util.OnTextEnteredListener() {
                            @Override
                            public void onTextEntered(String text) {
                                stage.setName(text);
                                name.setText(text);
                            }
                });
            }
        });
        name.setText(stage.getName());

        // the pause time
        pauseBefore.setText(Util.formatTime(stage.getPauseBefore()));
        pauseBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = stage.getPauseBefore(); // get the pause before to start with

                // display the time picker //TODO: remove hours from selection options
                MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getContext(), new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                        // update the stage and the display
                        stage.setPauseBefore(hourOfDay * 60 * 60 + minute * 60 + seconds);
                        pauseBefore.setText(Util.formatTime(stage.getPauseBefore()));
                    }
                }, Util.getHrs(t), Util.getMinsWithHrs(t), Util.getSecsWithMins(t), true);
                mTimePicker.show();
            }
        });

        // the duration
        duration.setText(Util.formatTime(stage.getTime()));
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = stage.getTime(); // get the time to start with

                // display the time picker //TODO: same as above
                MyTimePickerDialog mTimePicker = new MyTimePickerDialog(getContext(), new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                        // update the stage and the display
                        stage.setTime(hourOfDay * 60 * 60 + minute * 60 + seconds);
                        duration.setText(Util.formatTime(stage.getTime()));
                    }
                }, Util.getHrs(t), Util.getMinsWithHrs(t), Util.getSecsWithMins(t), true);
                mTimePicker.show();
            }
        });

        return convertView;
    }
}
