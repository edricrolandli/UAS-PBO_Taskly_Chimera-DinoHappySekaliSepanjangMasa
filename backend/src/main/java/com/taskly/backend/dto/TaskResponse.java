package com.taskly.backend.dto;

import com.taskly.backend.model.TaskStatus;
import com.taskly.backend.model.Priority;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDateTime deadline,
        int difficultyLevel,
        double estimatedHours,
        Priority priority,
        TaskStatus status,
        String taskType,
        String categoryName,
        LocalDateTime completedAt
) {}