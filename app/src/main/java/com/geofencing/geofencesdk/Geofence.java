package com.geofencing.geofencesdk;

import com.geofencing.stores.TokenStore;

public class Geofence {
    String userId;
    double latitude;
    double longitude;
    double accuracy;
    float speed;
    double altitude;
    float bearing;
    long timestamp;

    public Geofence(String user_id, double latitude, double longitude, double accuracy, float speed, double altitude, float bearing, long timestamp) {
        this.userId = user_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.altitude = altitude;
        this.bearing = bearing;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getBearing() {
        return bearing;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
