package com.geofencing.jobs;

import android.content.Context;

public class JobInitializer {

    public void scheduleJobs(Context context){
        CustomJobService.schedule(context, 1, SendEventsJob.class.getName());
    }
}
