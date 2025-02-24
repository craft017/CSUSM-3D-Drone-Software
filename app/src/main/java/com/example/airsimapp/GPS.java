package com.example.airsimapp;

public class GPS {
    private float latitude;
    private float longitude;
    private float altitude;

//use getGpsData from client.py

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
