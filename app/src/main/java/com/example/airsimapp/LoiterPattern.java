package com.example.airsimapp;
import java.util.ArrayList;
import java.util.Calendar;

public class LoiterPattern extends AutopilotCommand{
    private String pattern;
    private ArrayList<String> commands = new ArrayList<String>();
    private int commandCounter;
    public LoiterPattern(String Pattern, int hour, int minute){
        this.setId("loiterPattern");
        this.pattern = Pattern;
        this.commandCounter = 0;
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
    }
private void loadCommands(){
        if(this.pattern == "figureEight"){

        }
        else if(this.pattern == "raceTrack"){

        }
}
public void calculateCommand(float yawRate, float commandTime){

}
    public String getPattern() {
        return pattern;
    }
}
