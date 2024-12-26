package com.example.service.job;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JobScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final Scheduler scheduler;
    private final List<? extends QuartzScheduledJob> quartzScheduledJobs;

    public JobScheduler(final Scheduler scheduler, final List<? extends QuartzScheduledJob> quartzScheduledJobs) {
        this.scheduler = scheduler;
        this.quartzScheduledJobs = quartzScheduledJobs;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        for (final QuartzScheduledJob quartzJob : quartzScheduledJobs) {
            final String jobName = quartzJob.getJobName();
            final String triggerCron = quartzJob.triggerCron();

            final JobDetail jobDetail = JobBuilder.newJob(quartzJob.getClass())
                    .withIdentity(jobName)
                    .storeDurably()
                    .build();

            final Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(jobName + "Trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(triggerCron))
                    .build();

            final Set<Trigger> triggers = Set.of(trigger);

            scheduler.scheduleJob(jobDetail, triggers, true);
        }
    }

    public void triggerJob(String jobName) {
        try {
            scheduler.triggerJob(new JobKey(jobName));
        } catch (SchedulerException exception) {
            LOGGER.info("Failed to trigger job {}", jobName);
        }
    }
}
