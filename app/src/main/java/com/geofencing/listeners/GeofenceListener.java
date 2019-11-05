package com.geofencing.listeners;

import org.json.JSONException;

public interface GeofenceListener {
    void callback(String json) throws JSONException;
}
