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
                default:
                    Log.e("LoiterPattern", "Unknown pattern type");
            }
    }

    public void calculateCommand(GPS currentGPS, float currentHeading, float yawRate, float speed, float commandTime, Calendar startTime){
        if (!pattern.gotHeading) {
            desiredFirstTurn = (currentHeading + this.pattern.getDegrees()) % 360;
            desiredSecondTurn = (desiredFirstTurn + 180) % 360;
            firstLowerHeading = ((desiredFirstTurn - this.getHeadingTolerance()) % 360);
            firstUpperHeading = ((desiredFirstTurn + this.getHeadingTolerance()) % 360);
            pattern.setGotHeading(true);
        }


        if(!this.pattern.firstTurn && (currentHeading >= firstUpperHeading || currentHeading <= firstLowerHeading)){
            this.setCommandMessage("autopilot,forward_left," + yawRate + "," + speed + "," + commandTime);
        }
        else if(!this.pattern.firstTurn && (currentHeading <= firstUpperHeading || currentHeading >= firstLowerHeading)){
            this.pattern.setFirstTurn(true);
        }
        else if(!this.pattern.firstStraight && forwardCounter < (this.pattern.getRadius(yawRate, speed)/speed) *10){
            this.setCommandMessage("autopilot,forward," + yawRate + "," + speed + "," + commandTime);
            forwardCounter++;
        }
        else if(!this.pattern.firstStraight && (forwardCounter >= (this.pattern.getRadius(yawRate, speed)/speed) *10)){
            this.pattern.setFirstStraight(true);
            forwardCounter = 0;
            secondLowerHeading = ((desiredSecondTurn - this.getHeadingTolerance()) % 360);
            secondUpperHeading = ((desiredSecondTurn + this.getHeadingTolerance()) % 360);
        }
        else if(!this.pattern.secondTurn && (currentHeading >= secondUpperHeading || currentHeading <= secondLowerHeading)){
            this.setCommandMessage("autopilot,forward_left," + yawRate + "," + speed + "," + commandTime);
        }
        else if(!this.pattern.secondTurn && (currentHeading <= secondUpperHeading || currentHeading >= secondLowerHeading)){
            this.pattern.setSecondTurn(true);

        }
        else if(!this.pattern.secondStraight && (forwardCounter < (this.pattern.getRadius(yawRate, speed)/speed) *10)){
            this.setCommandMessage("autopilot,forward," + yawRate + "," + speed + "," + commandTime);
            forwardCounter++;
        }
        else if(forwardCounter >= (this.pattern.getRadius(yawRate, speed)/speed) *10){
            this.pattern.setSecondStraight(true);
            forwardCounter = 0;
        }
        else{
            this.pattern.setAllFlags(false);
        }
    }

    public String getPatternType() {
        return this.patternType;
    }
}
