package org.example.trackerscheduler.dto.user;

import org.example.trackerscheduler.dto.task.TaskSummaryDto;

import java.util.List;

public record UserDailyTaskSummary(
        Long userId,
        List<TaskSummaryDto> completedTasks,
        List<TaskSummaryDto> inProgressTasks
) {}