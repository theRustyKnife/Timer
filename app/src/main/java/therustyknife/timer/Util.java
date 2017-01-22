package therustyknife.timer;


import android.provider.ContactsContract;

public class Util {
    public static final String EXTRA_TIMER_KEY = "therustyknife.timer.extra_timer";


    public static final String formatTime(int seconds){
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
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
