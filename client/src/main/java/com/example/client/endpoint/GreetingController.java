package com.example.client.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.client.adapter.ServiceAdapter;

@RestController
public class GreetingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

    private final ServiceAdapter serviceAdapter;

    public GreetingController(ServiceAdapter serviceAdapter) {
        this.serviceAdapter = serviceAdapter;
    }

    @GetMapping("/job")
    public String launchJob(@RequestParam String jobName) {
        LOGGER.info("Received job launch request");
        serviceAdapter.triggerJob(jobName);
        return "Job has been launched!";
    }
}
