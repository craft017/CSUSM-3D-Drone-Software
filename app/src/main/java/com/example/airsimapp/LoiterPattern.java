package com.example.airsimapp;
import java.util.Calendar;
import java.util.Objects;

public class LoiterPattern extends AutopilotCommand{
    private int commandCounter;
    private int timeForCommand = 200;   //Default 200 ms
    private Pattern pattern;



    private String patternType;
    public LoiterPattern(String Pattern, int hour, int minute){
        this.setId("LoiterPattern");
        this.commandCounter = 0;
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
        this.patternType = Pattern;
    }

public void calculateCommand(float yawRate, float speed, Calendar startTime){
    this.setPattern(patternType, yawRate, speed);
    this.timeForCommand = this.pattern.currentCommandTime(this.commandCounter, yawRate, speed);
    this.setCommandMessage(this.pattern.getCommands().get(commandCounter));
    if(this.commandCounter >= this.pattern.getCommandLimit()){
        this.commandCounter = 0;
    }
    else {
        this.commandCounter++;
    }
}

    private void setPattern(String patternType, float yawRate, float speed) {
        if(this.pattern == null){
            if(Objects.equals(patternType, "RaceTrack")){
                this.pattern = new RaceTrack(yawRate, speed);
            }
            else if(Objects.equals(patternType, "FigureEight")){
                //TODO create figure eight pattern
            }
        }
        else{
            //Pattern already made, do nothing
        }
    }
    public int getTimeForCommand() {
        return timeForCommand;
    }

    public String getPatternType() {
        return patternType;
    }
}
