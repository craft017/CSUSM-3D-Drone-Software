package com.example.airsimapp;
import android.util.Log;

import java.util.Calendar;

public class LoiterPattern extends AutopilotCommand{
    private Pattern pattern;
    private final String patternType;
    private float desiredFirstTurn = 0;
    private float desiredSecondTurn = 0;
    private float firstLowerHeading = 0;
    private float firstUpperHeading = 0;
    private float secondLowerHeading = 0;
    private float secondUpperHeading = 0;
    private int forwardCounter = 0;
    public LoiterPattern(String Pattern,float yawRate, float speed, int hour, int minute){
        this.setId("LoiterPattern");
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
        this.patternType = Pattern;
        this.loadPattern(this.patternType, yawRate, speed);
    }
    private void loadPattern(String pattern, float yawRate, float speed){
            switch(pattern){
                case "RaceTrack":
                    this.pattern = new RaceTrack(yawRate, speed);
                    break;
                case "FigureEight":
                    this.pattern = new FigureEight(yawRate, speed);
                    break;
                case "Circle":
                    this.pattern = new Circle(yawRate, speed);
                    break;
                default:
                    Log.e("LoiterPattern", "Unknown pattern type");
            }
    }

    public void calculateCommand(float currentHeading, float yawRate, float speed, float commandTime, Calendar currentTime){
        if (pattern instanceof RaceTrack) {
            this.setCommandMessage(((RaceTrack) pattern).executePattern(currentHeading, yawRate, speed, commandTime, currentTime, getHeadingTolerance()));
        } else if (pattern instanceof Circle){
            this.setCommandMessage(((Circle) pattern).executePattern(yawRate, speed, commandTime, currentTime));
        }

        currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        if (getHourEndTime() == currentHour && getMinuteEndTime() == currentMinute) {
            this.setCommandMessage("autopilot,stop," + yawRate + "," + speed + "," + commandTime);
            setCommandComplete(true);
        }
    }

    public String getPatternType() {
        return this.patternType;
    }
}
