package com.example.service.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.job.JobScheduler;

@RestController
public class JobEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobEndpoint.class);

    private final JobScheduler jobScheduler;

    public JobEndpoint(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @GetMapping("/job")
    public void triggerJob(@RequestParam String jobName) {
        LOGGER.info("Received job trigger request");
        jobScheduler.triggerJob(jobName);
    }
}
