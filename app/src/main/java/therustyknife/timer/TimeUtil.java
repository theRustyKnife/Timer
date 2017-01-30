package therustyknife.timer;


import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.ArrayList;


public final class TimeUtil {
    // returns all the sessions from the day with the offset of days from now
    public static ArrayList<TimerStats.Session> getInDay(ArrayList<TimerStats.Session> sessions, int days){
        DateTime day = DateTime.now();
        day = day.plusDays(days);
        Interval interval = new Interval(day.withTimeAtStartOfDay(), day.plusDays(1).withTimeAtStartOfDay());

        ArrayList<TimerStats.Session> res = new ArrayList<TimerStats.Session>();
        for (TimerStats.Session s : sessions){
            if (interval.contains(s.getStartedAt())) res.add(s);
        }
        return res;
    }

    public static float getDayProgressPercent(long time){
        DateTime day = new DateTime(time);
        time -= day.withTimeAtStartOfDay().getMillis();

        return (float)(time / (24*60*60*1000.0));
    }

    public static float getDayPercent(long time) {
        return (float)(time / (24*60*60*1000.0));
    }
    public static float getDayPercent(int time){
        return getDayPercent(1000L * time);
    }
}
