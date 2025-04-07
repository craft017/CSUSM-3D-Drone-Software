package com.example.airsimapp;
import java.lang.Math;

public class HeadingAndSpeed extends AutopilotCommand{
    private float desiredHeading;
    private float desiredSpeed;

    public HeadingAndSpeed(float heading, float speed){
        this.desiredHeading = heading;
        this.desiredSpeed = speed;
    }

    public float getDesiredHeading() {
        return desiredHeading;
    }

    public float getDesiredSpeed() {
        return desiredSpeed;
    }

    public String calculateCommand(float currentHeading){
        String commandMessage = "NULL";
        float distanceToRight = (currentHeading - desiredHeading + 360) % 360;
        float distanceToLeft = (desiredHeading - currentHeading + 360) % 360;
        if(distanceToRight < distanceToLeft || distanceToRight == distanceToLeft){
            //Turning right

        }
        else if(distanceToRight > distanceToLeft){
            //Turning left

        }
        return commandMessage;
    }
}