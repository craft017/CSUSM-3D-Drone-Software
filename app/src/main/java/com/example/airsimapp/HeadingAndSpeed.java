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

    public void calculateCommand(float currentHeading, float yawRate, Calendar startTime ){
        String commandMessage;
        float headingDifference = Math.abs(desiredHeading - currentHeading);
        float turnDuration = (headingDifference/yawRate);

        float startMinutes = startTime.HOUR * 60 + startTime.MINUTE;
        float endMinutes = this.getHourEndTime() * 60 + this.getMinuteEndTime();
        float totalTime = Math.abs(endMinutes - startMinutes);
        float straightDuration = totalTime - turnDuration;

        float distanceToRight = (currentHeading - desiredHeading + 360) % 360;
        float distanceToLeft = (desiredHeading - currentHeading + 360) % 360;
        if(distanceToRight < distanceToLeft || distanceToRight == distanceToLeft){
            //Turning right
            commandMessage = "manual,right_turn," + yawRate + "," + desiredSpeed + "," + turnDuration;
            this.addToManualQueue(commandMessage);
        }
        else if(distanceToRight > distanceToLeft){
            //Turning left
            commandMessage = "manual,left_turn," + yawRate + "," + desiredSpeed + "," + turnDuration;
            this.addToManualQueue(commandMessage);
        }
        commandMessage = "manual,forward," + yawRate + "," + desiredSpeed + "," + straightDuration;
    }
}