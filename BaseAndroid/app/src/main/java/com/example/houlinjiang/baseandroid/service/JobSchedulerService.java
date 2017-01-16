package com.example.houlinjiang.baseandroid.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by houlin.jiang on 2017/1/11.
 */

public class JobSchedulerService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
