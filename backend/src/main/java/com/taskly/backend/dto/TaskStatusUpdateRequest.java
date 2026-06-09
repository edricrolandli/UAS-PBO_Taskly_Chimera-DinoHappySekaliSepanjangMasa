package com.taskly.backend.dto;

import com.taskly.backend.model.TaskStatus;

import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull TaskStatus status
) {}

