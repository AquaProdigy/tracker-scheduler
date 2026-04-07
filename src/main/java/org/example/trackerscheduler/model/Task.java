package org.example.trackerscheduler.model;

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
