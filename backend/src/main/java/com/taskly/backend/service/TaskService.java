package com.taskly.backend.service;

import com.taskly.backend.dto.TaskRequest;
import com.taskly.backend.dto.TaskResponse;
import com.taskly.backend.model.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, String userEmail);
    TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail);
    TaskResponse updateTaskStatus(Long taskId, TaskStatus status, String userEmail);

    void deleteTask(Long taskId, String userEmail);
    TaskResponse getTaskById(Long taskId, String userEmail);
    List<TaskResponse> getAllTasksByUser(String userEmail);
    List<TaskResponse> getReminders(String userEmail); // deadline <= 2 hari, status bukan COMPLETED
}



