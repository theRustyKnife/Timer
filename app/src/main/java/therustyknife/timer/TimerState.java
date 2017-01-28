package therustyknife.timer;


import android.os.Handler;
import android.util.Log;

public class TimerState {
    Timer timer;

    private boolean stopped = true; // true - not started, false started
    private boolean pause = true;   // true - currently in the pause part of the current stage, false - in the main part of the current stage

    private Session currentSession = null;

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

        timer.getStats().startNew();

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

    public void stop(boolean disruptive){
        if (disruptive && currentSession != null){
            int tTime = (int)(currentSession.getFinished() / Util.MILLIS_IN_SECOND);
            if (pause) timer.getStats().addPause(tTime);
            else timer.getStats().addTime(tTime);
        }
        timer.getStats().finish();
        position = -1;
        currentSession = null;
        stopped = true;
    }
    public void stop(){ stop(false); }


    // sets currentSession to the next session or calls stop() and returns false if done
    private boolean next(boolean skipping){
        if (pause){ // we're in a pause - get the stage
            pause = false;
            TimerStage c = getNext(position, true);

            if (currentSession != null) timer.getStats().addPause((int)(currentSession.getFinished() / Util.MILLIS_IN_SECOND));

            if (c == null){
                stop();
                return false;
            }
            currentSession = new Session((c.getTime()) * Util.MILLIS_IN_SECOND);
            return true;
        }
        else{ // we're in a stage - get the next stage's pause
            if (currentSession != null){
                int t = (int)(currentSession.getFinished() / Util.MILLIS_IN_SECOND);
                if (skipping) timer.getStats().addPause(t);
                else timer.getStats().addTime(t);
            }

            TimerStage tNext = getNext(position, false);

            if (tNext == null){ // nowhere further to go
                stop();
                return false;
            }
            pause = true;
            if (tNext.getPauseBefore() <= 0) return next(); // pause is 0 - go on
            else currentSession = new Session((tNext.getPauseBefore()) * Util.MILLIS_IN_SECOND);
            return true;
        }
    }
    private boolean next(){ return next(false); }
    public boolean skip(){ // same as next() except it always goes to the next stage
        if (pause) position++;
        timer.getStats().skip();
        boolean tP = pause;
        pause = false;
        return next(tP);
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


    class Session{
        private long startTime;
        private long duration;
        private long done;


        // create new session and sart it
        Session(long duration){
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

        long getFinished(){
            if (done != 0) return done;
            return System.currentTimeMillis() - startTime;
        }
    }
}
