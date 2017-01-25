package therustyknife.timer;


import java.io.Serializable;

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
    public TimerStage(String name, int time){ this(name, time, 0); }
    public TimerStage(String name){ this(name, 0, 0); }


    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public int getPauseBefore(){ return pauseBefore; }
    public void setPauseBefore(int pauseBefore){ this.pauseBefore = pauseBefore; }

    public int getTime(){ return time; }
    public void setTime(int time){ this.time = time; }

    public int getTotalLength(){ return pauseBefore + time; }
}
