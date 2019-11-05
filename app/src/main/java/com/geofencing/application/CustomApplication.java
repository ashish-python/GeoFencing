package com.geofencing.application;

import android.app.Application;

import com.geofencing.jobs.JobInitializer;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobInitializer jobInitializer = new JobInitializer();
        jobInitializer.scheduleJobs(getApplicationContext());
    }
}
