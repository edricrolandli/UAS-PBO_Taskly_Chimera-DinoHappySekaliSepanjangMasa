package com.taskly.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@DiscriminatorValue("GROUP")
public class GroupTask extends Task {

    @Override
    public void calculatePriority() {
        if (getDeadline() == null) return;
        long daysToDeadline = ChronoUnit.DAYS.between(LocalDateTime.now(), getDeadline());
        if (daysToDeadline <= 2) {
            setPriority(Priority.CRITICAL);
        } else if (daysToDeadline <= 4 || getDifficultyLevel() >= 4) {
            setPriority(Priority.HIGH);
        } else if (daysToDeadline <= 7) {
            setPriority(Priority.MEDIUM);
        } else {
            setPriority(Priority.LOW);
        }
    }
}
