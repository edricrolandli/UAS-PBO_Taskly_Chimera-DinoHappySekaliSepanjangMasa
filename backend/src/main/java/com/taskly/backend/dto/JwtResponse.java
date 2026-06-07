package com.taskly.backend.dto;

public record JwtResponse(
        String token,
        String email,
        String fullName,
        String role
) {}
