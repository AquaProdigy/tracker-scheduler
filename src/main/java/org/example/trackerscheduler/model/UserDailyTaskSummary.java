package org.example.trackerscheduler.model;

import java.util.List;

public record UserDailyTaskSummary(
        Long userId,
        List<TaskSummaryDto> completedTasks,
        List<TaskSummaryDto> inProgressTasks
) {}