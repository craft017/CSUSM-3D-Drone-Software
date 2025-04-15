package com.example.airsimapp;
import java.lang.Math;
import java.util.Calendar;

public class HeadingAndSpeed extends AutopilotCommand{
    private float desiredHeading;
    private float desiredSpeed;

    public HeadingAndSpeed(float heading, float speed, int hour, int minute){
        this.setId("heading&speed");
        this.desiredHeading = heading;
        this.desiredSpeed = speed;
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
    }

    public float getDesiredHeading() {
        return desiredHeading;
    }

    public float getDesiredSpeed() {
        return desiredSpeed;
    }

    public void calculateCommand(float currentHeading, float yawRate, float commandTime, Calendar startTime){
        float lower = ((desiredHeading-this.getHeadingTolerance())%360);
        float upper = ((desiredHeading+this.getHeadingTolerance())%360);
        if(currentHeading >= upper || currentHeading <= lower){
            float distanceToRight = (currentHeading - desiredHeading + 360) % 360;
            float distanceToLeft = (desiredHeading - currentHeading + 360) % 360;
            if(distanceToRight > distanceToLeft || distanceToRight == distanceToLeft){
                //Turning right
                this.setCommandMessage("manual,right_turn," + yawRate + "," + desiredSpeed + "," + commandTime);
            }
            else if(distanceToRight < distanceToLeft){
                //Turning left
                this.setCommandMessage("manual,left_turn," + yawRate + "," + desiredSpeed + "," + commandTime);
            }
        }
        else{
            this.setCommandMessage("manual,forward," + yawRate + "," + desiredSpeed + "," + commandTime);
        }
    }
}