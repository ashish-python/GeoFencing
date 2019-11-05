package com.geofencing.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.geofencing.constants.Constants;

@Entity (tableName = Constants.GEOFENCE_OBJECT_ENTITY)
public class GeofenceObjectEntity {

    @PrimaryKey
    @ColumnInfo(name = "geofence_id")
    @NonNull
    private String geofenceId;
    private double timestamp;
    private double latitude;
    private double longitude;
    private int radius;
    private int transitionEnter = 0;
    private int transitionExit = 0;
    private int transitionDwell = 0;

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getTransitionEnter() {
        return transitionEnter;
    }

    public void setTransitionEnter(int transitionEnter) {
        this.transitionEnter = transitionEnter;
    }

    public int getTransitionExit() {
        return transitionExit;
    }

    public void setTransitionExit(int transitionExit) {
        this.transitionExit = transitionExit;
    }

    public int getTransitionDwell() {
        return transitionDwell;
    }

    public void setTransitionDwell(int transitionDwell) {
        this.transitionDwell = transitionDwell;
    }
}
