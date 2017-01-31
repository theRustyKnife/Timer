package therustyknife.timer;


import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;


// this saves all the users stats about the particular timer
// see the list bellow
public class TimerStats implements Serializable {
    protected static final long serialVersionUID = 7897957532676179007L;

    // what's saved here in one way or another:
    // how many times the timer has been finished
    // how many times the timer has been aborted
    // time that the timer was running
    // time the timer was running without pauses
    // time the finished timers add up to

    // this is where everything is stored
    private ArrayList<Session> sessions;

    // the timer that these stats belong to
    private Timer timer;

    // the current session
    // doesn't get serialized as it's already contained in the list and after load it's no longer "current"
    private transient Session current;


    public TimerStats(Timer timer){
        this.timer = timer;
        sessions = new ArrayList<>();
    }


    public void startNew(){
        current = new Session(System.currentTimeMillis());
        sessions.add(current);
    }

    public void addTime(int time){
        if (current == null) startNew();
        current.time += time;
    }
    public void addPause(int pauseTime){
        if (current == null) startNew();
        current.pauses += pauseTime;
    }

    public void skip(){ current.skipped = true; }

    public void finish(){
        if (current != null) {
            current.finished = !current.skipped; // if no skips were made in this session, flag it as finished
            Log.d(Util.TAG, "finishing stat session: finished = " + current.finished + "; skipped = " + current.skipped + "; time = " + current.time + "; pauses = " + current.pauses);
            current = null;
        }
    }


    // get the overall time the timer has been running
    public int getRunTime(){
        int res = 0;
        for (Session s : sessions) res += s.time + s.pauses;
        return res;
    }

    // get the time the timer has been running in the given day
    public int getRunInDay(int dayOffset){
        int res = 0;
        for (Session s : TimeUtil.getInDay(sessions, dayOffset)){
            res += s.time + s.pauses;
        }
        return res;
    }

    // get all the sessions that were started in the given day
    public ArrayList<Session> getSessionsInDay(int dayOffset){
        return TimeUtil.getInDay(sessions, dayOffset);
    }


    // this class represents one time the timer has been run
    // this is where the data is actually stored
    public class Session implements Serializable {
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

        public int getLength(){ return time + pauses; }

        // work-around to find the timer this session belongs to from the global timer list to prevent deserialization issues caused by adding a new field
        public Timer getTimer(){
            for (Timer t : Timer.getList()){
                for (Session s : t.getStats().sessions)
                    if (s == this) return t;
            }
            return null;
        }
    }
}
