package com.example.airsimapp;
import java.util.Calendar;

public class LoiterPattern extends AutopilotCommand{
    private String pattern;
    //TODO create figure eight, racetrack, and maybe 1 more pattern
    public LoiterPattern(String Pattern, int hour, int minute){
        this.setId("LoiterPattern");
        this.pattern = Pattern;
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
    }
    public String getPattern() {
        return pattern;
    }
}
