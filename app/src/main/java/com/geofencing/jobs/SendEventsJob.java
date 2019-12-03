package com.geofencing.jobs;

import android.app.job.JobParameters;
import android.util.Log;

import com.geofencing.sync.EventsSyncer;

public class SendEventsJob extends CustomJobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        super.onStartJob(jobParameters);
        return doJob();
    }

    private boolean doJob() {
        EventsSyncer.getInstance(getApplicationContext()).syncGeofenceEvents();
        return true;
    }

}
