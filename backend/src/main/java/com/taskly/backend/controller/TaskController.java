package com.taskly.backend.controller;

import com.taskly.backend.dto.TaskRequest;
import com.taskly.backend.dto.TaskResponse;
import com.taskly.backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    // KONSTRUKTUR MANUAL (Mengeliminasi ketergantungan pada Lombok)
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            // Principal adalah UserDetails, username = email
            return auth.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        String email = getCurrentUserEmail();
        TaskResponse response = taskService.createTask(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request) {
        String email = getCurrentUserEmail();
        TaskResponse response = taskService.updateTask(id, request, email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                            @Valid @RequestBody com.taskly.backend.dto.TaskStatusUpdateRequest request) {
        String email = getCurrentUserEmail();
        TaskResponse response = taskService.updateTaskStatus(id, request.status(), email);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        taskService.deleteTask(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(taskService.getTaskById(id, email));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(taskService.getAllTasksByUser(email));
    }

    @GetMapping("/reminders")
    public ResponseEntity<List<TaskResponse>> getReminders() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(taskService.getReminders(email));
    }
}