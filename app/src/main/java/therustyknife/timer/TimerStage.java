package therustyknife.timer;


public class TimerStage {
    private String name;

    // times in seconds
    private int time;
    private int pauseBefore;
    private int pauseAfter;


    public TimerStage(String name, int time, int pauseBefore, int pauseAfter){
        this.time = time;
        this.pauseBefore = pauseBefore;
        this.pauseAfter = pauseAfter;
    }
    public TimerStage(String name, int time, int pauseBefore){ this(name, time, pauseBefore, 0); }
    public TimerStage(String name, int time){ this(name, time, 0, 0); }


    public int getTotalLength(){ return pauseBefore + time + pauseAfter; }
}
