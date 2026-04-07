package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TaskSchedulerService {
    private static final Integer TASK_IN_DESCRIPTION = 5;
    private static final String TEXT_FOR_ONGOING = "Today you have %d tasks in work";
    private static final String TEXT_FOR_COMPLETED = "Today you completed %d tasks";
    private static final String TITLE_TEXT_LETTER = "Your daily task report";
    private static final LocalDateTime TASKS_FOR_DAYS = LocalDateTime.now().toLocalDate().atStartOfDay();

    @Value("${internal.api-key}")
    private String internalApiKey;

    private final RestClient restClient;
    private final KafkaSenderService kafkaSenderService;

    @Scheduled(fixedRate = 10000)
    public void sendDailyReport() {
        List<User> users = getUsers();

        for (User user : users) {
            List<Task> tasks = getTasksByUser(user);

            Optional<String> body = createStringForEmail(tasks);

            if (body.isEmpty()) {
                return;
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
                    return task.updatedAt().isAfter(TASKS_FOR_DAYS);
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

    private List<Task> getTasksByUser(User user) {
        return restClient.get()
                .uri("http://localhost:8082/internal/tasks?userId=" + user.id())
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Task>>() {});
    }

    private List<User> getUsers() {
        return restClient.get()
                .uri("http://localhost:8081/internal/users")
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<List<User>>() {});
    }
}
