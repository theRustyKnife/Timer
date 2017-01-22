package therustyknife.timer;


public class TimerStage {
    private String name;

    // times in seconds
    private int time;
    private int pauseBefore;
    private int pauseAfter;


    public TimerStage(String name, int time, int pauseBefore, int pauseAfter){
        this.name = name;
        this.time = time;
        this.pauseBefore = pauseBefore;
        this.pauseAfter = pauseAfter;
    }
    public TimerStage(String name, int time, int pauseBefore){ this(name, time, pauseBefore, 0); }
    public TimerStage(String name, int time){ this(name, time, 0, 0); }
    public TimerStage(String name){ this(name, 0, 0, 0); }


    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public int getPauseBefore(){ return pauseBefore; }
    public void setPauseBefore(int pauseBefore){ this.pauseBefore = pauseBefore; }

    public int getTime(){ return time; }
    public void setTime(int time){ this.time = time; }

    public int getPauseAfter(){ return pauseAfter; }
    public void setPauseAfter(int pauseAfter){ this.pauseAfter = pauseAfter; }

    public int getTotalLength(){ return pauseBefore + time + pauseAfter; }
}
