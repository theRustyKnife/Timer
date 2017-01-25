package therustyknife.timer;


import android.os.Handler;
import android.util.Log;

public class TimerState {
    Timer timer;

    private boolean stopped = true; // true - not started, false started
    private boolean pause = true;   // true - currently in the pause part of the current stage, false - in the main part of the current stage

    private TimerRunSession currentSession = null;

    private int position = -1;

    public TimerState(Timer timer){
        this.timer = timer;
    }


    // start the timer, true if success
    public boolean start(){
        if (timer.getStageCount() == 0 || timer.getTotalLength() == 0) return false;
        Log.d(Util.TAG, "there are some stages in this timer");
        stopped = false;
        pause = false;

        position = -1;

        if (!next()) {
            Log.d(Util.TAG, "timer doesn't have any valid stages");
            return false;
        }
        Log.d(Util.TAG, "timer has some valid stages");

        final Handler h = new Handler();
        final int delay = 100; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                update();
                if (!stopped) h.postDelayed(this, delay);
            }
        }, delay);

        return true;
    }

    public void update(){
        if (currentSession == null || currentSession.isDone()) next();
    }

    public void pause(){
        currentSession.pause();
    }

    public void resume(){
        currentSession.resume();
    }

    public void stop(){
        position = -1;
        stopped = true;
    }


    // sets currentSession to the next session or calls stop() and returns false if done
    private boolean next(){
        if (pause){ // we're in a pause - get the stage
            pause = false;
            TimerStage c = getNext(position, true);
            if (c == null){
                stop();
                return false;
            }
            currentSession = new TimerRunSession((c.getTime() + 1) * Util.MILLIS_IN_SECOND);
            return true;
        }
        else{ // we're in a stage - get the next stage's pause
            TimerStage tNext = getNext(position, false);
            if (tNext == null){ // nowhere further to go
                stop();
                return false;
            }
            pause = true;
            if (tNext.getPauseBefore() <= 0) return next(); // pause is 0 - go on
            else currentSession = new TimerRunSession((tNext.getPauseBefore() + 1) * Util.MILLIS_IN_SECOND);
            return true;
        }
    }
    public boolean skip(){ // same as next() except it always goes to the next stage
        if (pause) position++;
        pause = false;
        return next();
    }


    private boolean prev(boolean breakIfZero){
        pause = true;
        if (position <= 0){
            if (breakIfZero) return false;
            position = 0;
        }
        else position--;
        if (getCurrent().getTime() <= 0)
            if (!prev(true)) return next();
        position--;
        return next();
    }
    public boolean prev(){ return prev(false); }


    public boolean isRunning(){
        if (currentSession != null) return !currentSession.isPaused();
        return false;
    }
    public boolean isStopped(){ return stopped; }
    public boolean isPaused(){
        if (currentSession != null) return currentSession.isPaused();
        return false;
    }

    public TimerStage getCurrent(){ return timer.getStage(position); }
    // returns the next non-zero stage or null if no such stage exists
    private TimerStage getNext(int pos, boolean increment){
        if (pos < -1 || pos > timer.getStageCount() - 2) return null; // out of bounds

        TimerStage tStage = timer.getStage(++pos);
        if (increment) position = pos;

        if (tStage.getTime() <= 0) return getNext(pos, increment); // stage has zero length - continue

        return tStage;
    }
    public TimerStage getNext(){ return getNext(position, false); }

    public String getStatus(){
        if (timer.getStageCount() == 0) return Util.context.getString(R.string.add_stage_hint);
        if (stopped) return Util.context.getString(R.string.start);
        if (!stopped && pause) return Util.context.getString(R.string.pause);
        if (!stopped && !pause) return getCurrent().getName();

        Log.wtf(Util.TAG, "Alright, something went terribly RONG. You shouldn't be seeing this.");
        return "";
    }

    public String getRemainingTime(){
        if (currentSession == null) return Util.formatTime(0);
        return Util.formatTime(currentSession.getRemaining());
    }
}


class TimerRunSession{
    private long startTime;
    private long duration;
    private long done;


    // create new session and sart it
    TimerRunSession(long duration){
        this.duration = duration;
        startTime = System.currentTimeMillis();
    }
    // pause if not already paused
    void pause(){
        if (done == 0) done = System.currentTimeMillis() - startTime;
    }
    //resume if not already resumed
    void resume(){
        startTime = System.currentTimeMillis() - done;
        done = 0;
    }
    // true if done and not paused - should be deleted if done
    boolean isDone(){
        return done == 0 && System.currentTimeMillis() - startTime >= duration;
    }

    boolean isPaused(){ return done != 0; }

    long getRemaining(){
        long t = startTime - System.currentTimeMillis() + duration;
        if (done != 0) return duration - done;
        if (t < 0) return 0;
        return t;
    }
}