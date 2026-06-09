package com.taskly.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record TaskRequest(
        @NotBlank(message = "Judul tidak boleh kosong")
        String title,

        String description,

        @NotNull(message = "Tenggat waktu wajib diisi")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Sinkronisasi otomatis format ISO String -> LocalDateTime
        LocalDateTime deadline,

        @Min(1) @Max(5)
        int difficultyLevel,

        @Positive
        double estimatedHours,

        @NotBlank(message = "Tipe tugas wajib dipilih")
        String taskType,

        Long categoryId,

        String priority,

        String status
) {}
