package com.example.service.job.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.example.service.job.QuartzScheduledBatchJob;
import com.example.service.model.Person;

@Component
public class PersonBatchJob extends QuartzScheduledBatchJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PersonBatchTasklet personBatchTasklet;

    @Autowired
    private PersonBatchReader personBatchReader;

    @Autowired
    private PersonBatchProcessor personBatchProcessor;

    @Autowired
    private PersonBatchWriter personBatchWriter;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public Job getJobInstance() {
        return jobBuilderFactory.get(getJobName())
                .start(getSerialStep())
                .next(getParallelStep())
                .build();
    }

    private Step getSerialStep() {
        return stepBuilderFactory.get("person-batch-job-step1")
                .tasklet(personBatchTasklet)
                .build();
    }

    private Step getParallelStep() {
        return stepBuilderFactory.get("person-batch-job-step2")
                .<Person, Person>chunk(250)
                .reader(personBatchReader)
                .processor(personBatchProcessor)
                .writer(personBatchWriter)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Override
    public String getJobName() {
        return "person-batch-job";
    }

    @Override
    public String triggerCron() {
        return "0 0 0 * * ?";
    }
}
