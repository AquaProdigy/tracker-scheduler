package org.example.trackerscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trackerscheduler.client.TaskServiceClient;
import org.example.trackerscheduler.client.UserEmailServiceClient;
import org.example.trackerscheduler.dto.email.EmailLetterModel;
import org.example.trackerscheduler.dto.task.TaskSummaryDto;
import org.example.trackerscheduler.dto.user.InternalUserEmailDto;
import org.example.trackerscheduler.dto.user.UserDailyTaskSummary;
import org.example.trackerscheduler.service.TaskSchedulerService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerServiceImpl implements TaskSchedulerService {
    private static final Integer TASK_IN_DESCRIPTION = 5;
    private static final String TEXT_FOR_IN_PROGRESS= "Today you have %d tasks in work";
    private static final String TEXT_FOR_COMPLETED = "Today you completed %d tasks";
    private static final String TITLE_TEXT_LETTER = "Your daily task report";

    private final TaskServiceClient taskServiceClient;
    private final UserEmailServiceClient userEmailServiceClient;
    private final org.example.trackerscheduler.service.KafkaSenderService kafkaSenderService;

    @Override
    public void sendDailyReport() {
        List<UserDailyTaskSummary> tasksToday = taskServiceClient.getSummaryTasks();

        if (tasksToday.isEmpty()) {
            return;
        }

        List<Long> ids = tasksToday.stream()
                .map(UserDailyTaskSummary::userId)
                .toList();

        Map<Long, String> emailById = userEmailServiceClient.getEmailByIds(ids)
                .stream()
                .collect(Collectors.toMap(InternalUserEmailDto::id, InternalUserEmailDto::email));

        tasksToday.forEach(summary -> {
            String email = emailById.get(summary.userId());

            if (email == null) {
                return;
            }

            boolean hasCompleted = !summary.completedTasks().isEmpty();
            boolean hasInProgress = !summary.inProgressTasks().isEmpty();

            if (!hasCompleted && !hasInProgress) {
                return;
            }

            kafkaSenderService.sendMessageToKafka(
                    new EmailLetterModel(
                        email,
                        TITLE_TEXT_LETTER,
                        buildDescription(summary)
                    )
            );

        });

    }

    private String buildDescription(UserDailyTaskSummary summary) {
        StringBuilder description = new StringBuilder();

        if (!summary.completedTasks().isEmpty()) {
            appendTasks(description, TEXT_FOR_COMPLETED, summary.completedTasks());
        }

        if (!summary.inProgressTasks().isEmpty()) {
            appendTasks(description, TEXT_FOR_IN_PROGRESS, summary.inProgressTasks());
        }

        return description.toString();
    }

    private void appendTasks(StringBuilder sb, String template, List<TaskSummaryDto> tasks) {
        sb.append(template.formatted(tasks.size()));
        tasks.stream()
                .limit(TASK_IN_DESCRIPTION)
                .forEach(task -> sb.append("• ").append(task.title()).append("\n"));
    }




}
