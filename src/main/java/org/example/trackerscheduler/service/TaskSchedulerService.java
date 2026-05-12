package org.example.trackerscheduler.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface TaskSchedulerService {
    @Scheduled(cron = "${scheduler.daily-report.cron}")
    void sendDailyReport();
}
