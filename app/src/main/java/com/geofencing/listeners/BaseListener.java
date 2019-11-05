package com.geofencing.listeners;

import android.content.Context;

public interface BaseListener {
    void callback(Context context, Integer status, String responseString);
}
