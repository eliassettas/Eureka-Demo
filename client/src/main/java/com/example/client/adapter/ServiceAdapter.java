package com.example.client.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service")
public interface ServiceAdapter {

    @GetMapping("job")
    void triggerJob(@RequestParam String jobName);
}
