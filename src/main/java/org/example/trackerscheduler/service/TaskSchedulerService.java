package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trackerscheduler.model.EmailLetterModel;
import org.example.trackerscheduler.model.Task;
import org.example.trackerscheduler.model.TaskStatus;
import org.example.trackerscheduler.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerService {
    private static final Integer TASK_IN_DESCRIPTION = 5;
    private static final String TEXT_FOR_ONGOING = "Today you have %d tasks in work";
    private static final String TEXT_FOR_COMPLETED = "Today you completed %d tasks";
    private static final String TITLE_TEXT_LETTER = "Your daily task report";

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Value("${services.auth-url}")
    private String authUrl;

    @Value("${services.task-url}")
    private String taskUrl;

    private final RestClient restClient;
    private final KafkaSenderService kafkaSenderService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendDailyReport() {
        List<User> users = getUsers();
        log.info("Sending Daily Report for {} users", users.size());

        for (User user : users) {
            List<Task> tasks = getTasksByUser(user);
            log.info("Sending Daily Report for {} tasks", tasks.size());

            Optional<String> body = createStringForEmail(tasks);
            log.info(body.toString());
            if (body.isEmpty()) {
                continue;
            }

            kafkaSenderService.sendMessageToKafka(
                    new EmailLetterModel(
                            user.email(),
                            TITLE_TEXT_LETTER,
                            body.get()
                    )
            );
        }

    }

    private Optional<String> createStringForEmail(List<Task> tasks) {
        List<Task> ongoingTasks = tasks.stream()
                .filter(task -> {
                  return task.status() == TaskStatus.ONGOING;
        }).toList();

        List<Task> completedTasks = tasks.stream()
                .filter(task -> {
                    return task.status() == TaskStatus.COMPLETED;
                })
                .filter(task -> {
                    return task.updatedAt().isAfter(getStartOfDay());
                })
                .toList();

        if (ongoingTasks.isEmpty() &&  completedTasks.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder body = new StringBuilder();

        if (!ongoingTasks.isEmpty()) {
            body.append(TEXT_FOR_ONGOING.formatted(ongoingTasks.size())).append(System.lineSeparator());

            ongoingTasks.stream().limit(TASK_IN_DESCRIPTION).forEach(task -> {
                body.append(task.title()).append(System.lineSeparator());
            });
        }

        if (!completedTasks.isEmpty()) {
            body.append(TEXT_FOR_COMPLETED.formatted(completedTasks.size())).append(System.lineSeparator());

            completedTasks.stream().limit(TASK_IN_DESCRIPTION).forEach(task -> {
                body.append(task.title()).append(System.lineSeparator());
            });
        }

        return Optional.of(body.toString());
    }

    private LocalDateTime getStartOfDay() {
        return LocalDateTime.now().toLocalDate().atStartOfDay();
    }

    private List<Task> getTasksByUser(User user) {
        return restClient.get()
                .uri(taskUrl + "/internal/tasks?userId=" + user.id())
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Task>>() {});
    }

    private List<User> getUsers() {
        return restClient.get()
                .uri(authUrl + "/internal/users")
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<List<User>>() {});
    }
}
