package com.taskly.backend.service;

import com.taskly.backend.dto.TaskRequest;
import com.taskly.backend.dto.TaskResponse;
import com.taskly.backend.model.*;
import com.taskly.backend.repository.TaskRepository;
import com.taskly.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // KONSTRUKTUR MANUAL (Solusi Mutlak Mengatasi Duplikasi & Kendala Inisialisasi Lombok)
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private Task createTaskByType(String taskType) {
        return switch (taskType.toUpperCase()) {
            case "ASSIGNMENT" -> new AssignmentTask();
            case "EXAM" -> new ExamTask();
            case "GROUP" -> new GroupTask();
            default -> throw new IllegalArgumentException("Invalid task type");
        };
    }

    @Override
    public TaskResponse createTask(TaskRequest request, String userEmail) {

        System.out.println("[DEBUG] TaskService.createTask() - userEmail: " + userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    System.err.println("[ERROR] User not found for email: " + userEmail);
                    return new RuntimeException("User not found");
                });
        
        System.out.println("[DEBUG] User found: " + user.getEmail() + " (ID: " + user.getId() + ")");

        Task task = createTaskByType(request.taskType());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        task.setDifficultyLevel(request.difficultyLevel());
        task.setEstimatedHours(request.estimatedHours());
        task.setUser(user);
        task.setStatus(TaskStatus.PENDING);

        // Polymorphism: Otomatis memicu calculatePriority milik masing-masing subclass khusus
        task.calculatePriority();

        taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        task.setDifficultyLevel(request.difficultyLevel());
        task.setEstimatedHours(request.estimatedHours());

        if (request.status() != null && !request.status().isBlank()) {
            TaskStatus newStatus = TaskStatus.valueOf(request.status().toUpperCase());
            if (newStatus == TaskStatus.COMPLETED) {
                if (task.getStatus() != TaskStatus.COMPLETED) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            } else {
                task.setCompletedAt(null);
            }
            task.setStatus(newStatus);
        }

        // Prioritas selalu dihitung secara dinamis oleh sistem (tidak boleh diubah manual)
        task.calculatePriority();

        taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public void deleteTask(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }
        taskRepository.delete(task);
    }

    @Override
    public TaskResponse getTaskById(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasksByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByUserId(user.getId())
                .stream()
                .sorted(this::compareTasks)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private int compareTasks(Task t1, Task t2) {
        // 1. Priority descending: CRITICAL -> HIGH -> MEDIUM -> LOW
        int p1 = getPriorityWeight(t1.getPriority());
        int p2 = getPriorityWeight(t2.getPriority());
        if (p1 != p2) {
            return Integer.compare(p2, p1);
        }

        // 2. Deadline ascending: earlier deadline first
        if (t1.getDeadline() != null && t2.getDeadline() != null) {
            int cmp = t1.getDeadline().compareTo(t2.getDeadline());
            if (cmp != 0) return cmp;
        } else if (t1.getDeadline() != null) {
            return -1;
        } else if (t2.getDeadline() != null) {
            return 1;
        }

        // 3. Estimated hours descending: longer hours first
        return Double.compare(t2.getEstimatedHours(), t1.getEstimatedHours());
    }

    private int getPriorityWeight(Priority priority) {
        if (priority == null) return 2; // MEDIUM
        return switch (priority) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    @Override
    public List<TaskResponse> getReminders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysLater = now.plusDays(2);
        List<Task> tasks = taskRepository.findByUserId(user.getId());
        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED
                        && task.getDeadline() != null
                        && task.getDeadline().isBefore(twoDaysLater)
                        && task.getDeadline().isAfter(now.minusMinutes(1)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }

        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        taskRepository.save(task);
        return mapToResponse(task);
    }

    private TaskResponse mapToResponse(Task task) {

        String categoryName = task.getCategory() != null ? task.getCategory().getCategoryName() : null;
        String taskType = task.getClass().getSimpleName().replace("Task", "").toUpperCase();
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getDifficultyLevel(),
                task.getEstimatedHours(),
                task.getPriority(),
                task.getStatus(),
                taskType,
                categoryName,
                task.getCompletedAt()
        );
    }
}
