package org.example.trackerscheduler.model.user;

import org.example.trackerscheduler.model.task.TaskSummaryDto;

import java.util.List;

public record UserDailyTaskSummary(
        Long userId,
        List<TaskSummaryDto> completedTasks,
        List<TaskSummaryDto> inProgressTasks
) {}