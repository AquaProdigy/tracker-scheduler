package org.example.trackerscheduler.dto.task;

import java.time.LocalDateTime;

public record Task(
        Long id,
        Long userId,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
