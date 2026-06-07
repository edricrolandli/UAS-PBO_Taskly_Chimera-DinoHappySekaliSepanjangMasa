package com.taskly.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@DiscriminatorValue("EXAM")
public class ExamTask extends Task {

    @Override
    public void calculatePriority() {
        if (getDeadline() == null) return;
        long daysToDeadline = ChronoUnit.DAYS.between(LocalDateTime.now(), getDeadline());
        if (daysToDeadline <= 2 && getDifficultyLevel() >= 3) {
            setPriority(Priority.CRITICAL);
        } else if (daysToDeadline <= 5) {
            setPriority(Priority.HIGH);
        } else if (daysToDeadline <= 10) {
            setPriority(Priority.MEDIUM);
        } else {
            setPriority(Priority.LOW);
        }
    }
}
