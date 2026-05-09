package org.example.trackerscheduler.client;

import org.example.trackerscheduler.config.FeignConfig;
import org.example.trackerscheduler.model.UserDailyTaskSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@FeignClient(name = "tracker-task", url = "${services.task-url}${services-url.task-endpoint-daily-summary}",
        configuration = FeignConfig.class)
public interface TaskServiceClient {

    @GetMapping
    List<UserDailyTaskSummary> getSummaryTasks();

}
