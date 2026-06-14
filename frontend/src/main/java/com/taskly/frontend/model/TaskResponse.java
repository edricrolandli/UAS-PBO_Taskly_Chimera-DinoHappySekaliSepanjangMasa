package com.taskly.frontend.model;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String deadline; // ISO string
    private int difficultyLevel;
    private double estimatedHours;
    private String priority;
    private String status;
    private String taskType;
    private String categoryName;

    // KONSTRUKTUR KOSONG
    public TaskResponse() {}

    // GETTER & SETTER MANUAL (Solusi Mutlak untuk Menghilangkan Error Dashboard)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    private String completedAt;
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}
