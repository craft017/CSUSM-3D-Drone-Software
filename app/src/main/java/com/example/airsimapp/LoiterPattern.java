package com.example.airsimapp;

public class LoiterPattern extends AutopilotCommand{
    private String pattern;
    //TODO create figure eight, racetrack, and maybe 1 more pattern
    public LoiterPattern(String Pattern){
        this.setId("loiterPattern");
        this.pattern = Pattern;
    }
    public String getPattern() {
        return pattern;
    }
}
