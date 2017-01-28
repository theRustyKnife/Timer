package therustyknife.timer;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;


public class TimerStats implements Serializable {
    protected static final long serialVersionUID = 7897957532676179007L;

    // we're going to save the users progress in a timer here
    // how many times the timer has been finished
    // how many times the timer has been aborted
    // time that the timer was running
    // time the timer was running without pauses
    // time the finished timers add up to

    private ArrayList<Session> sessions;

    private Timer timer;

    private transient Session current;


    public TimerStats(Timer timer){
        this.timer = timer;
        sessions = new ArrayList<Session>();
    }


    public void startNew(){
        Log.d(Util.TAG, "starting new stat session");
        current = new Session(System.currentTimeMillis());
        sessions.add(current);
    }

    public void addTime(int time){
        if (current == null) startNew();
        Log.d(Util.TAG, "adding " + time + " to current session");
        current.time += time;
    }
    public void addPause(int pauseTime){
        if (current == null) startNew();
        Log.d(Util.TAG, "adding " + pauseTime + " to current pause");
        current.pauses += pauseTime;
    }

    public void skip(){
        Log.d(Util.TAG, "skipping in current stat session");
        current.skipped = true;
    }

    public void finish(){
        if (current != null) {
            current.finished = !current.skipped;
            Log.d(Util.TAG, "finishing stat session: finished = " + current.finished + "; skipped = " + current.skipped + "; time = " + current.time + "; pauses = " + current.pauses);
            current = null;
        }
    }


    public int getRunTime(){
        int res = 0;
        for (Session s : sessions) res += s.time + s.pauses;
        return res;
    }

    public int getRunInDay(int dayOffset){
        int res = 0;
        for (Session s : TimeUtil.getInDay(sessions, dayOffset)){
            res += s.time + s.pauses;
        }
        return res;
    }


    class Session implements Serializable {
        protected static final long serialVersionUID = -2746575701570301509L;

        private long startedAt = 0;

        private boolean finished = false;
        private boolean skipped = false;
        private int time = 0;
        private int pauses = 0;


        public Session(long startedAt){
            this.startedAt = startedAt;
        }


        public long getStartedAt(){ return startedAt; }
    }
}
