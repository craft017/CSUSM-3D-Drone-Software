package com.example.airsimapp;

public class HeadingAndSpeed extends AutopilotCommand{
    private float heading;
    private float speed;

    public float getHeading() {
        return heading;
    }

    public float getSpeed() {
        return speed;
    }

    public float convertHeading360(float currentHeading){
        float heading360 = 0;       //Set heading to 0(forward) as baseline
        if(currentHeading > 0){
            return currentHeading;
        }
        else{
            heading360 = (180 + currentHeading) + 180;
        }
        return heading360;
    }
}