package com.example.airsimapp;
import android.util.Log;

import java.util.Calendar;

public class LoiterPattern extends AutopilotCommand{
    private Pattern pattern;
    private final String patternType;
    private float firstDesiredLongitude;
    private float firstDesiredLatitude;
    private float secondDesiredLongitude;
    private float secondDesiredLatitude;
    public LoiterPattern(String Pattern, int hour, int minute){
        this.setId("LoiterPattern");
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
        this.patternType = Pattern;
        this.firstDesiredLatitude = 0;
        this.firstDesiredLongitude = 0;
        float secondDesiredLongitude = 0;
        float secondDesiredLatitude = 0;
    }
    private void loadPattern(String pattern, float yawRate, float speed){
        if(this.pattern == null){
            //Do nothing
        }
        else{
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
    }
    private float meterToGPSCoordinate(float meters){
        return meters*0.00001f;
    }
    private void resetDesiredGPS(){
        this.firstDesiredLatitude = 0;
        this.firstDesiredLongitude = 0;
        float secondDesiredLongitude = 0;
        float secondDesiredLatitude = 0;
    }

    public void calculateCommand(GPS currentGPS, float currentHeading, float yawRate, float speed, float commandTime, Calendar startTime){
        this.loadPattern(this.patternType, yawRate, speed);
        float desiredFirstTurn = (currentHeading + this.pattern.getDegrees()) % 360;
        float desiredSecondTurn = (currentHeading + this.pattern.getDegrees()) % 360;
        float firstLowerHeading = ((desiredFirstTurn-this.getHeadingTolerance())%360);
        float firstUpperHeading = ((desiredSecondTurn+this.getHeadingTolerance())%360);
        float secondLowerHeading = ((desiredFirstTurn-this.getHeadingTolerance())%360);
        float secondUpperHeading = ((desiredSecondTurn+this.getHeadingTolerance())%360);
        float headingQuadrant;
        if(currentHeading >= 0 && currentHeading <= 90){
            headingQuadrant = currentHeading;
        }
        else if(currentHeading >= 91 && currentHeading <= 180){
            headingQuadrant = currentHeading-90;
        }
        else if(currentHeading >= 181 && currentHeading <= 270){
            headingQuadrant = currentHeading-180;
        }
        else if(currentHeading >= 271 && currentHeading <= 360){
            headingQuadrant = currentHeading-270;
        }

        if(this.firstDesiredLongitude == 0 && this.firstDesiredLatitude == 0){
            firstDesiredLongitude = currentGPS.getLongitude() + meterToGPSCoordinate((float) (this.pattern.straightDistance * Math.sin(headingQuadrant)));
            firstDesiredLatitude = currentGPS.getLatitude() + meterToGPSCoordinate((float) (this.pattern.straightDistance * Math.cos(headingQuadrant)));
        }

        if(!this.pattern.firstTurn && currentHeading >= firstUpperHeading && currentHeading <= firstLowerHeading){
            this.setCommandMessage("autopilot,forward_left," + yawRate + "," + speed + "," + commandTime);
        }
        else if(currentHeading <= firstUpperHeading || currentHeading >= firstLowerHeading){
            this.pattern.setFirstTurn(true);
        }
        else if(!this.pattern.firstStraight && currentGPS.getLongitude() >= firstDesiredLongitude + this.getGpsTolerance() && currentGPS.getLongitude() <= firstDesiredLongitude - this.getGpsTolerance() && currentGPS.getLatitude() >= firstDesiredLatitude + this.getGpsTolerance() && currentGPS.getLatitude() <= firstDesiredLatitude - this.getGpsTolerance()){
            this.setCommandMessage("autopilot,forward," + yawRate + "," + speed + "," + commandTime);
        }
        else if(currentGPS.getLongitude() <= firstDesiredLongitude + this.getGpsTolerance() && currentGPS.getLongitude() >= firstDesiredLongitude - this.getGpsTolerance() && currentGPS.getLatitude() <= firstDesiredLatitude + this.getGpsTolerance() && currentGPS.getLatitude() >= firstDesiredLatitude - this.getGpsTolerance()){
            this.pattern.setFirstStraight(true);
            if(this.secondDesiredLatitude == 0 && this.secondDesiredLongitude == 0){
                secondDesiredLongitude = currentGPS.getLongitude() + meterToGPSCoordinate((float) (this.pattern.straightDistance * Math.sin(headingQuadrant)));
                secondDesiredLatitude = currentGPS.getLatitude() + meterToGPSCoordinate((float) (this.pattern.straightDistance * Math.cos(headingQuadrant)));
            }
        }
        else if(!this.pattern.secondTurn && currentHeading >= secondUpperHeading && currentHeading <= secondLowerHeading){
            this.setCommandMessage("autopilot,forward_left," + yawRate + "," + speed + "," + commandTime);
        }
        else if(currentHeading <= secondUpperHeading || currentHeading >= secondLowerHeading){
            this.pattern.setSecondTurn(true);

        }
        else if(!this.pattern.secondStraight && currentGPS.getLongitude() >= secondDesiredLongitude + this.getGpsTolerance() && currentGPS.getLongitude() <= secondDesiredLongitude - this.getGpsTolerance() && currentGPS.getLatitude() >= secondDesiredLatitude + this.getGpsTolerance() && currentGPS.getLatitude() <= secondDesiredLatitude - this.getGpsTolerance()){
            this.setCommandMessage("autopilot,forward," + yawRate + "," + speed + "," + commandTime);
        }
        else if(currentGPS.getLongitude() >= secondDesiredLongitude + this.getGpsTolerance() && currentGPS.getLongitude() <= secondDesiredLongitude - this.getGpsTolerance() && currentGPS.getLatitude() >= secondDesiredLatitude + this.getGpsTolerance() && currentGPS.getLatitude() <= secondDesiredLatitude - this.getGpsTolerance()){
            this.pattern.setSecondStraight(true);
        }
        else{
            this.resetDesiredGPS();
            this.pattern.setAllFlags(false);
        }
    }
}
