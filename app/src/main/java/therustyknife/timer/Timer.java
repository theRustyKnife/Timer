package therustyknife.timer;


import android.content.Context;
import android.graphics.Path;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Timer implements Serializable{
    private static final TimerComparator comparator = new TimerComparator();

    private static ArrayList<Timer> list = new ArrayList<Timer>();


    private String displayName;
    private String name;
    private ArrayList<TimerStage> stages;


    public static ArrayList<Timer> getList(){ return list; }

    public static final void sortList(){ Collections.sort(list, comparator); }


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


    public Timer(String name, ArrayList<TimerStage> stages){
        this.displayName = name;
        this.stages = stages;
        this.name = makeNameUnique(name);
    }
    public Timer(String name){ this(name, new ArrayList<TimerStage>()); }


    public static void saveList(Context context){
        for (Timer t : list) t.save(context);
    }

    public static void loadList(Context context){
        File dir = new File(Util.makePath(Util.TIMER_SAVE_PATH));
        Log.d("tagofallthetags", "attempting to load timers from " + dir.getPath());
        if (!dir.exists() || dir.isFile()){
            dir.mkdirs();
        }

        list = new ArrayList<Timer>();

        String[] files = dir.list();
        if (files == null) return;

        for (String fname : files){
            File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + fname));
            if (file.isFile()){
                list.add(load(context, fname));
            }
        }
    }


    public void addStage(TimerStage stage){ stages.add(stage); }

    public void save(Context context){
        File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + name));
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
            os.flush();
            Util.writeData(file.getPath(), bos.toByteArray());
            bos.close();
        }catch (IOException e){
            Log.d("tagofallthetags", "failed save to " + file.getPath(), e);
        }
    }

    public static Timer load(Context context, String path){
        File file = new File(Util.makePath(Util.TIMER_SAVE_PATH + path));
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(Util.readData(file.getPath()));
            ObjectInputStream is = new ObjectInputStream(bis);
            Timer res = (Timer) is.readObject();
            is.close();
            bis.close();
            return res;
        }catch(IOException | ClassNotFoundException e){
            Log.d("tagofallthetags", "failed load from " + file.getPath(), e);
            return null;
        }
    }

    public void delete(){
        Util.deleteFile(Util.makePath(Util.TIMER_SAVE_PATH + name));
        for (int i = 0; i < list.size(); i++){
            if (list.get(i) == this) list.remove(i);
        }
    }

    public ArrayList<TimerStage> getStages(){ return stages; }

    public String getName(){ return displayName; }

    public int getStageCount(){ return stages.size(); }

    public int getTotalLength(){
        int res = 0;
        for (TimerStage stage : stages) res += stage.getTotalLength();
        return res;
    }
}


class TimerComparator implements Comparator<Timer>{
    @Override
    public int compare(Timer timer, Timer t1) {
        return timer.getName().compareTo(t1.getName());
    }
}