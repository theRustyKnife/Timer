package therustyknife.timer;


import java.io.Serializable;


// this class represents a stage in a timer, with duration and a pause
public class TimerStage implements Serializable {
    private String name;

    // times in seconds
    private int time;
    private int pauseBefore;


    public TimerStage(String name, int time, int pauseBefore){
        this.name = name;
        this.time = time;
        this.pauseBefore = pauseBefore;
    }
    public TimerStage(String name, int time){ this(name, time, 10); }
    public TimerStage(String name){ this(name, 30, 10); }


    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public int getPauseBefore(){ return pauseBefore; }
    public void setPauseBefore(int pauseBefore){ this.pauseBefore = pauseBefore; }

    public int getTime(){ return time; }
    public void setTime(int time){ this.time = time; }

    public int getTotalLength(){ return pauseBefore + time; }
}
