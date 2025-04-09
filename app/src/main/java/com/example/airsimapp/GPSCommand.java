package com.example.airsimapp;
import java.util.Calendar;

public class GPSCommand extends AutopilotCommand{
    private float latitude;
    private float longitude;
    private float altitude;

    public GPSCommand(float lat, float lon, float alt, int hour, int minute){
        this.setId("gps");
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
        this.setHourEndTime(hour);
        this.setMinuteEndTime(minute);
    }

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