package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trackerscheduler.client.TaskServiceClient;
import org.example.trackerscheduler.client.UserEmailServiceClient;
import org.example.trackerscheduler.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerService {
    private static final Integer TASK_IN_DESCRIPTION = 5;
    private static final String TEXT_FOR_IN_PROGRESS= "Today you have %d tasks in work";
    private static final String TEXT_FOR_COMPLETED = "Today you completed %d tasks";
    private static final String TITLE_TEXT_LETTER = "Your daily task report";

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Value("${services.auth-url}")
    private String authUrl;

    @Value("${services.task-url}")
    private String taskUrl;

    private final TaskServiceClient taskServiceClient;
    private final UserEmailServiceClient userEmailServiceClient;
    private final KafkaSenderService kafkaSenderService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendDailyReport() {
        List<UserDailyTaskSummary> tasksToday = taskServiceClient.getSummaryTasks();

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

            if (!hasCompleted || !hasInProgress) {
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
            description.append(TEXT_FOR_COMPLETED.formatted(summary.completedTasks().size()));

            summary.completedTasks().stream().limit(TASK_IN_DESCRIPTION).forEach(task -> {
                description.append(task.title());
            });
        }

        if (!summary.inProgressTasks().isEmpty()) {
            description.append(TEXT_FOR_IN_PROGRESS.formatted(summary.inProgressTasks().size()));

            summary.completedTasks().stream().limit(TASK_IN_DESCRIPTION).forEach(task -> {
                description.append(task.title());
            });
        }

        return description.toString();
    }




}
