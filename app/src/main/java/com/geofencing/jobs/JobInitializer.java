package com.geofencing.jobs;

import android.content.Context;

/**
 * This class is used to set up jobs that will run at regular intervals even if the application is in the background or killed
 * Some of these are:
 * Sending geofence event data to the server
 * Get geofences list for the child
 */
public class JobInitializer {
    public void scheduleJobs(Context context){
        CustomJobService.schedule(context, 1, SendEventsJob.class.getName());
    }
}
