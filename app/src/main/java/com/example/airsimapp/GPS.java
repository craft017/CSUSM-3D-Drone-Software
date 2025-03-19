package com.example.airsimapp;

public class GPS extends AutopilotCommand{
    private float latitude;
    private float longitude;
    private float altitude;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getAltitude() {
        return altitude;
    }
}