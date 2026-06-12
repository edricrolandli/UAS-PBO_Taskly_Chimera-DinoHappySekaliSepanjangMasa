package com.taskly.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taskly.frontend.config.ApiConfig;
import com.taskly.frontend.model.TaskResponse;
import com.taskly.frontend.util.HttpClientUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {

    public static List<TaskResponse> getAllTasks() throws Exception {
        String json = HttpClientUtil.sendGet(ApiConfig.BASE_URL + "/tasks", AuthService.getToken());
        return HttpClientUtil.getMapper().readValue(json, new TypeReference<List<TaskResponse>>() {});
    }

    public static List<TaskResponse> getReminders() throws Exception {
        String json = HttpClientUtil.sendGet(ApiConfig.BASE_URL + "/tasks/reminders", AuthService.getToken());
        return HttpClientUtil.getMapper().readValue(json, new TypeReference<List<TaskResponse>>() {});
    }

    public static TaskResponse getTaskById(Long taskId) throws Exception {
        String json = HttpClientUtil.sendGet(ApiConfig.BASE_URL + "/tasks/" + taskId, AuthService.getToken());
        return HttpClientUtil.getMapper().readValue(json, TaskResponse.class);
    }

    public static TaskResponse createTask(String title, String description, String deadlineStr,
                                          int difficultyLevel, double estimatedHours, String taskType) throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description != null ? description : "");
        body.put("deadline", deadlineStr);
        body.put("difficultyLevel", difficultyLevel);
        body.put("estimatedHours", estimatedHours);
        body.put("taskType", taskType);
        body.put("categoryId", null);

        return HttpClientUtil.sendPost(ApiConfig.BASE_URL + "/tasks", body, TaskResponse.class, AuthService.getToken());
    }

    public static TaskResponse updateTask(Long taskId, String title, String description, String deadlineStr,
                                           int difficultyLevel, double estimatedHours, String taskType,
                                           String priority, String status) throws Exception {

        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description != null ? description : "");
        body.put("deadline", deadlineStr);
        body.put("difficultyLevel", difficultyLevel);
        body.put("estimatedHours", estimatedHours);
        body.put("taskType", taskType);
        body.put("categoryId", null);
        body.put("priority", priority);
        body.put("status", status);

        return HttpClientUtil.sendPut(ApiConfig.BASE_URL + "/tasks/" + taskId, body, TaskResponse.class, AuthService.getToken());
    }

    public static void deleteTask(Long taskId) throws Exception {
        HttpClientUtil.sendDelete(ApiConfig.BASE_URL + "/tasks/" + taskId, AuthService.getToken());
    }

    public static TaskResponse updateTaskStatus(Long taskId, String newStatus) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("status", newStatus);

        // endpoint backend: /api/tasks/{id}/status
        return HttpClientUtil.sendPut(
                ApiConfig.BASE_URL + "/tasks/" + taskId + "/status",
                body,
                TaskResponse.class,
                AuthService.getToken()
        );

    }

}

