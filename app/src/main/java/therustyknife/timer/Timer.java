package therustyknife.timer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


// the main timer class
// this holds all the data and gets serialized
public class Timer implements Serializable{
    // an id to prevent deserialization issues
    protected static final long serialVersionUID = -28997423111818821L;


    // the global timer list
    private static ArrayList<Timer> list = new ArrayList<>();


    // the stats for this timer
    private TimerStats stats;

    // the current state of this timer, doesn't get serialized and is always reinstantiated before use
    private transient TimerState state;

    // the general timer properties
    private String displayName; // the name that the user can see and change
    private String name; // the name that is unique to this timer and can't be changed
    private int color = Util.context.getResources().getColor(R.color.colorPrimary); // the color of this timer, default to the color of the app
    private ArrayList<TimerStage> stages; // the list of stages for this timer


    // make a unique, identifying name to use as filename
    // appends a number suffix to the name if it already exists
    //TODO: check this code again, doesn't seem quite correct (but the app works with it)
    public static String makeNameUnique(String name, int n){
        String suf = "";
        if (n >= 0) suf += n;
        for (Timer t : list){
            if (t.name.equals(name + suf)){
                return makeNameUnique(name + (n + 1));
            }
        }
        return name + suf;
    }
    public static String makeNameUnique(String name){ return makeNameUnique(name, -1); }


    // serialize the global timer list
    public static void saveList(){ for (Timer t : list) t.save(); }

    // load the global timer list from the serialized state
    public static void loadList(){
        // get the directory to load timers from
        File dir = new File(Util.makePath(Util.TIMER_SAVE_PATH));
        // make sure the directory exists
        if (!dir.exists() || dir.isFile()) dir.mkdirs();

        list = new ArrayList<>();

        // get the file list from the directory, return if none are found
        String[] files = dir.list();
        if (files == null) return;

        // iterate over the files and load timers from them
        for (String fname : files){
            File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + fname));
            if (file.isFile()){
                list.add(load(fname));
            }
        }
    }


    public static ArrayList<Timer> getList(){ return list; }


    public Timer(String name, ArrayList<TimerStage> stages){
        stats = new TimerStats(this);

        this.displayName = name;
        this.stages = stages;
        this.name = makeNameUnique(name);

        initState();
    }
    public Timer(String name){ this(name, new ArrayList<TimerStage>()); }

    // create a new instance of TimerState for this timer
    public void initState(){ state = new TimerState(this); }


    // serialize this timer
    public void save(){
        // get the path to save this timer to
        File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + name));

        try {
            // make sure the file exists before writing to it
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            // serialize to a byte array and then to the file (no idea why I thought this was a good idea)
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
            os.flush();
            Util.writeData(file.getPath(), bos.toByteArray());
            bos.close();
        }catch (IOException e){}
    }

    // load a timer from the specified path
    public static Timer load(String path){
        File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + path));
        try {
            // load to a byte array and then deserialize (again, I have no clue why I did it this way)
            ByteArrayInputStream bis = new ByteArrayInputStream(Util.readData(file.getPath()));
            ObjectInputStream is = new ObjectInputStream(bis);
            Timer res = (Timer) is.readObject();
            is.close();
            bis.close();

            res.initState();

            return res;
        }catch(IOException | ClassNotFoundException e){ return null; } // return null on failure
    }

    // delete this timer
    public void delete(){
        // delete the serialized file
        Util.deleteFile(Util.makePath(Util.TIMER_SAVE_PATH + name));

        // remove from the global list
        for (int i = 0; i < list.size(); i++){
            if (list.get(i) == this) list.remove(i);
        }
    }


    public void addStage(TimerStage stage){ stages.add(stage); }


    public ArrayList<TimerStage> getStages(){ return stages; }
    // get the stage at the specified index
    public TimerStage getStage(int index){
        if (index >= 0 && index < stages.size()) return stages.get(index);
        return null;
    }

    public String getName(){ return displayName; }
    public void setName(String name){ this.displayName = name; }

    public int getStageCount(){ return stages.size(); }

    // return the length of this timer, including pauses
    public int getTotalLength(){
        int res = 0;
        for (TimerStage stage : stages) res += stage.getTotalLength();
        return res;
    }

    // return the length of this timer excluding pauses
    public int getLengthWithoutPauses(){
        int res = 0;
        for (TimerStage stage : stages) res += stage.getTime();
        return res;
    }

    public int getColor(){ return color; }
    public void setColor(int color){ this.color = color; }

    public TimerState getState(){ return state; }

    public TimerStats getStats(){ return stats; }
}
