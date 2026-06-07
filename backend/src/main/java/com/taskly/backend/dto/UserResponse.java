package com.taskly.backend.dto;

public record UserResponse(Long id, String fullName, String email, String role) {}