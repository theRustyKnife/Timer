package therustyknife.timer;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

public class Util {
    public static final String EXTRA_TIMER_KEY = "therustyknife.timer.extra_timer";
    public static final String TIMER_SAVE_PATH = "/timers/";

    public static final long MILLIS_IN_SECOND = 1000;

    public static final String TAG = "therustyknife.timer";

    public static Context context;

    public static final String formatTime(int seconds){
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
    public static final String formatTime(long milis){ return formatTime((int)(milis / MILLIS_IN_SECOND)); }

    public static String makePath(String path){ return context.getFilesDir() + path; }

    public static boolean canWriteOnExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void showConfirmBox(Activity a, String title, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        AlertDialog alertDialog = new AlertDialog.Builder(a).create();
        alertDialog.setTitle(title);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText, positiveListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, negativeListener);

        alertDialog.show();
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

    public static final DataHolder getDataHolder(){ return DataHolder.getInstance(); }
}


class DataHolder {
    private Object data;
    public Object getData(){ return data; }
    public void setData(Object data){ this.data = data; }

    private static final DataHolder instance = new DataHolder();
    public static DataHolder getInstance(){ return instance; }
}
