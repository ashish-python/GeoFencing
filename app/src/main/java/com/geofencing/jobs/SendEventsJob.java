package com.geofencing.jobs;

import android.app.job.JobParameters;
import android.util.Log;

public class SendEventsJob extends CustomJobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        super.onStartJob(jobParameters);
        return doJob();
    }

    private boolean doJob() {
        Log.v("EVENT_STARTED", "EVENT STARTED");
        return true;
    }

}
