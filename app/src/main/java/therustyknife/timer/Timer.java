package therustyknife.timer;


import java.util.ArrayList;

public class Timer {
    private String name;

    private ArrayList<TimerStage> stages;


    public Timer(String name, ArrayList<TimerStage> stages){
        this.name = name;
        this.stages = stages;
    }
    public Timer(String name){ this(name, new ArrayList<TimerStage>()); }


    public void addStage(TimerStage stage){ stages.add(stage); }


    public String getName(){ return name; }

    public int getStageCount(){ return stages.size(); }

    public int getTotalLength(){
        int res = 0;
        for (TimerStage stage : stages) res += stage.getTotalLength();
        return res;
    }
}
