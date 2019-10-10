package com.geofencing.interfaces;

import org.json.JSONException;

public interface GeofenceInterface {
    void addGeofences(String json) throws JSONException;
}
