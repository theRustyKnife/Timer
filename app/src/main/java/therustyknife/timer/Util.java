package therustyknife.timer;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import therustyknife.timer.Activity.MainActivity;

public class Util {
    public static final String PREFS_KEY = "timer_prefs";
    public static final String TIMER_SAVE_PATH = "/timers/";

    public static final String TIME_FORMAT_PREF_KEY = "time_format_index";
    public static final String[] TIME_FORMATS = new String[]{ "%1$02d:%2$02d", "" };
    public static int timeFormatIndex = 0;

    public static final long MILLIS_IN_SECOND = 1000;

    public static final String TAG = "therustyknife.timer";


    public static Context context;


    public static final String formatTime(int seconds){
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(TIME_FORMATS[timeFormatIndex], minutes, seconds);
    }
    public static final String formatTime(long milis){ return formatTime((int)(milis / MILLIS_IN_SECOND)); }

    public static int getHrs(int t){ return t / (60 * 60); }
    public static int getMinsWithHrs(int t){ return (t / 60) % 60; }
    public static int getSecsWithMins(int t){ return t % 60; }

    public static String makePath(String path){ return context.getFilesDir() + path; }
    public static String makePathExt(String path){ return Environment.getExternalStorageDirectory() + path; }

    public static boolean canWriteOnExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static float dpToPx(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static float pxToDp(float px){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }

    public static void showConfirmBox(Activity a, String title, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        AlertDialog alertDialog = new AlertDialog.Builder(a).create();
        alertDialog.setTitle(title);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText, positiveListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, negativeListener);

        alertDialog.show();
    }
    public static void showConfirmBox(Activity a, String title, DialogInterface.OnClickListener positiveListener){
        showConfirmBox(a, title, a.getString(R.string.ok), a.getString(R.string.cancel), positiveListener, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    public static void showTextQuery(Activity a, String title, String description, String hint, String positiveText, String negativeText, String text, final OnTextEnteredListener listener){
        AlertDialog alertDialog = new AlertDialog.Builder(a).create();
        alertDialog.setTitle(title);

        final View v = a.getLayoutInflater().inflate(R.layout.text_query_layout, null);
        TextView label = (TextView) v.findViewById(R.id.text_query_label);
        final EditText input = (EditText) v.findViewById(R.id.text_query_text);
        label.setText(description);
        input.setHint(hint);
        if (text != null) input.setText(text);

        alertDialog.setView(v);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String res = input.getText().toString();
                if (res.equals("")) res = input.getHint().toString();
                listener.onTextEntered(res);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();
    }
    public static void showTextQuery(Activity a, String title, String description, String hint, String positiveText, String text, final OnTextEnteredListener listener){
        showTextQuery(a, title, description, hint, positiveText, a.getString(R.string.cancel), text, listener);
    }
    public static void showTextQuery(Activity a, String title, String description, String hint, String positiveText, final OnTextEnteredListener listener){
        showTextQuery(a, title, description, hint, positiveText, null, listener);
    }
    public static void showTextQuery(Activity a, String title, String description, String hint, final OnTextEnteredListener listener){
        showTextQuery(a, title, description, hint, a.getString(R.string.ok), listener);
    }

    public static void writeData(String path, byte[] data){
        File file = new File(path);
        try{
            if (!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(file, false);
            stream.write(data);
            stream.close();
        }catch(IOException e){
            Log.d(Util.TAG, "failed write to " + file.getPath(), e);
        }
    }

    public static byte[] readData(String path){
        File file = new File(path);
        try{
            if (!file.exists()) return new byte[0];

            FileInputStream stream = new FileInputStream(file);

            byte[] data = new byte[(int)file.length()];
            int off = 0;
            while (off < data.length){
                int n = stream.read(data, off, data.length - off);
                if (n < 0) break;
                off += n;
            }

            stream.close();

            return data;
        }catch (IOException e){
            Log.d(Util.TAG, "failed to read from " + file.getPath(), e);
            return new byte[0];
        }
    }

    public static void deleteFile(String path){
        File file = new File(path);
        if (file.exists()) file.delete();
    }


    public static DataHolder getDataHolder(){ return DataHolder.getInstance(); }
    public static class DataHolder {
        private Object data;
        public Object getData(){ return data; }
        public void setData(Object data){ this.data = data; }

        private static final DataHolder instance = new DataHolder();
        public static DataHolder getInstance(){ return instance; }
    }

    public interface OnTextEnteredListener{
        public void onTextEntered(String text);
    }
}

