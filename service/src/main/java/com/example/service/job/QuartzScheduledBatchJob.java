package com.example.service.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class QuartzScheduledBatchJob extends QuartzScheduledJob {

    @Autowired
    private JobLauncher jobLauncher;

    public abstract Job getJobInstance();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            jobLauncher.run(getJobInstance(), new JobParameters());
        } catch (Exception exception) {
            throw new JobExecutionException(exception);
        }
    }
}
