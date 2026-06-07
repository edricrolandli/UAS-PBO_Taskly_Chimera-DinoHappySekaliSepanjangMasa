package com.taskly.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank @Email(message = "Email must be valid")
        String email,

        @NotBlank @Size(min = 8, message = "Password minimal 8 characters")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {}