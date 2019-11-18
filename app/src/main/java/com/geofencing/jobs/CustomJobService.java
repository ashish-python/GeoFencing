package com.geofencing.jobs;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

public abstract class CustomJobService extends JobService {
    public static final int RUN_JOBS_DEFAULT_INTERVAL_IN_MILLIS = 15 * 60 * 1000; //15 minutes
    protected JobParameters jobParameters;

    public static void cancelAll(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters, true);
        return true;
    }

    public static void schedule(Context context, int jobId, String jobName) {
        schedule(context, jobId, jobName, RUN_JOBS_DEFAULT_INTERVAL_IN_MILLIS);
    }

    public static void schedule(Context context, int jobId, String jobName, int jobIntervalMillis) {
        Log.v("JOB_SCHEDULER", "Inside CustomJObService schedule()");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null && !isJobAlreadyScheduled(jobScheduler, jobId)) {
            jobScheduler.schedule(new JobInfo.Builder(jobId, new ComponentName(context, jobName))
                    .setPeriodic(jobIntervalMillis)
                    .build());
        }
        Log.v("JOB_SCHEDULER", "Jobs Scheduled");
    }

    private static boolean isJobAlreadyScheduled(JobScheduler jobScheduler, int jobId) {
        List<JobInfo> jobInfoList = jobScheduler.getAllPendingJobs();
        for (JobInfo jobInfo : jobInfoList) {
            if (jobInfo.getId() == jobId) {
                return true;
            }
        }
        return false;
    }
}
