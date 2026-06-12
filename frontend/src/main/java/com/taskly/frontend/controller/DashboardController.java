package com.taskly.frontend.controller;

import com.taskly.frontend.model.TaskResponse;
import com.taskly.frontend.service.AuthService;
import com.taskly.frontend.service.TaskService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private Label greetingLabel;
    @FXML private Label totalTasksLabel, completedLabel, reminderCountLabel;
    @FXML private VBox taskListBox;
    @FXML private VBox completedListBox;
    @FXML private VBox reminderListBox;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private ComboBox<String> prioritySortCombo;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox activeTaskSection;
    @FXML private VBox completedSection;
    @FXML private VBox reminderSection;

    private List<TaskResponse> taskList;

    @FXML
    public void initialize() {
        typeFilterCombo.getItems().addAll("Semua Tipe", "ASSIGNMENT", "EXAM", "GROUP");
        typeFilterCombo.setValue("Semua Tipe");
        typeFilterCombo.setOnAction(e -> renderFilteredTasks());

        prioritySortCombo.getItems().addAll("Semua Prioritas", "CRITICAL", "HIGH", "MEDIUM", "LOW");
        prioritySortCombo.setValue("Semua Prioritas");
        prioritySortCombo.setOnAction(e -> renderFilteredTasks());

        loadData();
    }

    // --- Data Loading ---

    private void loadData() {
        try {
            taskList = TaskService.getAllTasks();

            String fullName = AuthService.getCurrentUserFullName();
            if (greetingLabel != null)
                greetingLabel.setText("Selamat datang, " + fullName + "! 👋");

            long total     = taskList.size();
            long completed = taskList.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("COMPLETED"))
                    .count();

            totalTasksLabel.setText(String.valueOf(total));
            completedLabel.setText(String.valueOf(completed));

            renderFilteredTasks();
            renderReminderCards();

        } catch (Exception e) {
            showAlert("Error", "Gagal memuat data: " + e.getMessage());
        }
    }

    private void renderFilteredTasks() {
        if (taskList == null) return;

        String selectedType = typeFilterCombo.getValue();
        if (selectedType == null) selectedType = "Semua Tipe";
        final String finalType = selectedType;

        String selectedPriority = prioritySortCombo.getValue();
        if (selectedPriority == null) selectedPriority = "Semua Prioritas";
        final String finalPriority = selectedPriority;

        List<TaskResponse> activeList = taskList.stream()
                .filter(t -> t.getStatus() == null || !t.getStatus().equalsIgnoreCase("COMPLETED"))
                .filter(t -> "Semua Tipe".equalsIgnoreCase(finalType)
                        || (t.getTaskType() != null && t.getTaskType().equalsIgnoreCase(finalType)))
                .filter(t -> "Semua Prioritas".equalsIgnoreCase(finalPriority)
                        || (t.getPriority() != null && t.getPriority().equalsIgnoreCase(finalPriority)))
                .sorted(this::compareTasks)
                .collect(Collectors.toList());

        List<TaskResponse> completedList = taskList.stream()
                .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("COMPLETED"))
                .filter(t -> "Semua Tipe".equalsIgnoreCase(finalType)
                        || (t.getTaskType() != null && t.getTaskType().equalsIgnoreCase(finalType)))
                .filter(t -> "Semua Prioritas".equalsIgnoreCase(finalPriority)
                        || (t.getPriority() != null && t.getPriority().equalsIgnoreCase(finalPriority)))
                .collect(Collectors.toList());

        taskListBox.getChildren().clear();
        if (activeList.isEmpty()) {
            Label empty = buildEmptyLabel("Tidak ada tugas aktif. Tambahkan tugas baru! 🎉");
            taskListBox.getChildren().add(empty);
        } else {
            for (TaskResponse task : activeList) {
                taskListBox.getChildren().add(buildTaskCard(task));
            }
        }

        completedListBox.getChildren().clear();
        if (completedList.isEmpty()) {
            Label empty = buildEmptyLabel("Belum ada tugas yang diselesaikan.");
            completedListBox.getChildren().add(empty);
        } else {
            for (TaskResponse task : completedList) {
                completedListBox.getChildren().add(buildCompletedCard(task));
            }
        }
    }

    // --- Card Builders ---

    private VBox buildTaskCard(TaskResponse task) {
        String priority = task.getPriority() != null ? task.getPriority().toUpperCase() : "MEDIUM";

        // Card container
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 24px;" +
            "-fx-border-color: #eaeaea;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 24px;" +
            "-fx-padding: 20px 22px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 24px;" +
            "-fx-border-color: #68b0ab;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 24px;" +
            "-fx-padding: 20px 22px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 24px;" +
            "-fx-border-color: #eaeaea;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 24px;" +
            "-fx-padding: 20px 22px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        ));

        // --- Row 1: Title + Priority badge + Action buttons ---
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: 600; -fx-text-fill: #2b2b2b;");
        titleLabel.setWrapText(false);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Label priorityBadge = buildPriorityBadge(priority);

        Button completeBtn = buildLabeledButton("✓  Selesai", "#28a745", "#218838");
        Button editBtn     = buildLabeledButton("✏  Edit",    "#17a2b8", "#138496");
        Button deleteBtn   = buildLabeledButton("🗑  Hapus",  "#dc3545", "#c82333");

        HBox topRow = new HBox(12, titleLabel, priorityBadge, completeBtn, editBtn, deleteBtn);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // --- Row 2: Description ---
        String descText = task.getDescription() != null && !task.getDescription().isBlank()
                ? task.getDescription()
                : "Tidak ada deskripsi";
        boolean hasDesc = task.getDescription() != null && !task.getDescription().isBlank();
        Label descLabel = new Label(descText);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (hasDesc ? "#7a7a7a" : "#c0c0c0") + ";"
                + (hasDesc ? "" : " -fx-font-style: italic;"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(Double.MAX_VALUE);

        // --- Row 3: Metadata chips ---
        HBox chips = new HBox(8);
        chips.setAlignment(Pos.CENTER_LEFT);
        chips.getChildren().add(buildTypeChip(task.getTaskType()));
        chips.getChildren().add(buildChip(task.getEstimatedHours() + " jam", "#f0f0f0", "#5a5a5a"));
        chips.getChildren().add(buildChip("Sulit " + task.getDifficultyLevel(), "#f0f0f0", "#5a5a5a"));
        if (task.getDeadline() != null) {
            String deadlineShort = formatDeadlineDateOnly(task.getDeadline());
            chips.getChildren().add(buildChip("📅 " + deadlineShort, "#fff3cd", "#856404"));
        }

        card.getChildren().addAll(topRow, descLabel, chips);

        // --- Event handlers ---
        completeBtn.setOnAction(e -> handleUpdateStatus(task, "COMPLETED"));
        editBtn.setOnAction(e -> handleEditTask(task));
        deleteBtn.setOnAction(e -> handleDeleteTask(task));

        return card;
    }

    private HBox buildCompletedCard(TaskResponse task) {
        Label titleLabel = new Label("✓  " + task.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0a0a0; -fx-font-style: italic; -fx-strikethrough: true;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        String completedStr = task.getCompletedAt() != null ? formatCompletedTimestamp(task.getCompletedAt()) : "-";
        Label dateLabel = new Label(completedStr);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #b0b0b0;");

        HBox row = new HBox(12, titleLabel, dateLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: #fafafa;" +
            "-fx-background-radius: 14px;" +
            "-fx-border-color: #f0f0f0;" +
            "-fx-border-radius: 14px;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 12px 16px;"
        );
        return row;
    }

    // --- Reminder Cards ---

    private void renderReminderCards() {
        List<TaskResponse> reminders = taskList.stream()
                .filter(t -> t.getStatus() != null && !t.getStatus().equalsIgnoreCase("COMPLETED") && t.getDeadline() != null)
                .filter(t -> {
                    boolean isCritical = t.getPriority() != null && t.getPriority().equalsIgnoreCase("CRITICAL");
                    if (isCritical) return true;
                    try {
                        String dateStr = t.getDeadline().substring(0, 10);
                        return !LocalDate.parse(dateStr).isAfter(LocalDate.now().plusDays(2));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        reminderCountLabel.setText(String.valueOf(reminders.size()));
        reminderListBox.getChildren().clear();

        if (reminders.isEmpty()) {
            Label empty = buildEmptyLabel("Tidak ada deadline mendekat.");
            reminderListBox.getChildren().add(empty);
            return;
        }

        for (TaskResponse task : reminders) {
            reminderListBox.getChildren().add(buildReminderCard(task));
        }
    }

    private VBox buildReminderCard(TaskResponse task) {
        String prio = task.getPriority() != null ? task.getPriority().toUpperCase() : "MEDIUM";

        // Urgency tint system
        String bgColor, borderColor;
        switch (prio) {
            case "CRITICAL" -> { bgColor = "rgba(220,53,69,0.07)";   borderColor = "#f5c6cb"; }
            case "HIGH"     -> { bgColor = "rgba(255,140,66,0.07)";  borderColor = "#ffd3b5"; }
            case "MEDIUM"   -> { bgColor = "rgba(244,184,96,0.08)";  borderColor = "#ffe8a3"; }
            default         -> { bgColor = "rgba(168,213,255,0.08)"; borderColor = "#bee3f8"; }
        }

        // Title
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 13px; -fx-text-fill: #2b2b2b;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(240);

        // Type badge + priority badge row
        String typeText = task.getTaskType() != null ? task.getTaskType().toUpperCase() : "TUGAS";
        Label typeBadge = buildTypeChip(typeText);
        Label prioBadge = buildPriorityBadge(prio);
        HBox badgeRow = new HBox(8, typeBadge, prioBadge);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        // Deadline date + weekday
        String deadlineStr = formatDeadlineDateOnly(task.getDeadline());
        String datePart = deadlineStr.contains("(") ? deadlineStr.substring(0, deadlineStr.indexOf("(")).trim() : deadlineStr;
        String dayPart  = deadlineStr.contains("(") ? deadlineStr.replaceAll(".*\\((.*)\\).*", "$1") : "";

        Label dateLabel = new Label("📅 " + datePart);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #2b2b2b;");
        Label dayLabel = new Label(dayPart);
        dayLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a9a9a;");

        VBox card = new VBox(6, titleLabel, badgeRow, dateLabel, dayLabel);
        String baseStyle =
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14px;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 14px;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 12px 14px;";
        String hoverStyle =
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 14px;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 14px;" +
            "-fx-border-width: 1.5px;" +
            "-fx-padding: 12px 14px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);";
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        return card;
    }

    // --- Widget Helpers ---

    private Button buildLabeledButton(String label, String baseColor, String hoverColor) {
        Button btn = new Button(label);
        String base  = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600;" +
            "-fx-background-radius: 8; -fx-padding: 6 12; -fx-cursor: hand;", baseColor);
        String hover = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600;" +
            "-fx-background-radius: 8; -fx-padding: 6 12; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 6, 0, 0, 2);", hoverColor);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private Button buildIconButton(String icon, String baseColor, String hoverColor) {
        Button btn = new Button(icon);
        String base  = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px;" +
            "-fx-background-radius: 50; -fx-min-width: 32; -fx-min-height: 32;" +
            "-fx-pref-width: 32; -fx-pref-height: 32; -fx-cursor: hand;", baseColor);
        String hover = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px;" +
            "-fx-background-radius: 50; -fx-min-width: 32; -fx-min-height: 32;" +
            "-fx-pref-width: 32; -fx-pref-height: 32; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 1);", hoverColor);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private Label buildPriorityBadge(String priority) {
        Label badge = new Label(priority);
        String style = switch (priority.toUpperCase()) {
            case "LOW"      -> "-fx-text-fill: #2e5fa3; -fx-background-color: #d6eaff;";
            case "MEDIUM"   -> "-fx-text-fill: #856404; -fx-background-color: #fff3cd;";
            case "HIGH"     -> "-fx-text-fill: #d2691e; -fx-background-color: #ffe4cc;";
            case "CRITICAL" -> "-fx-text-fill: #c33030; -fx-background-color: #fde8e8;";
            default         -> "-fx-text-fill: #5a5a5a; -fx-background-color: #efefef;";
        };
        badge.setStyle(style + " -fx-background-radius: 20; -fx-padding: 4 10; -fx-font-size: 11px; -fx-font-weight: bold;");
        return badge;
    }

    private Label buildTypeChip(String type) {
        String typeUpper = type != null ? type.toUpperCase() : "TUGAS";
        String style = switch (typeUpper) {
            case "ASSIGNMENT" -> "-fx-text-fill: #1d3557; -fx-background-color: #a8dadc;";
            case "EXAM"       -> "-fx-text-fill: #5e35b1; -fx-background-color: #d1c4e9;";
            case "GROUP"      -> "-fx-text-fill: #e65100; -fx-background-color: #ffe0b2;";
            default           -> "-fx-text-fill: #3c3c3c; -fx-background-color: #e8e3dc;";
        };
        Label chip = new Label(typeUpper);
        chip.setStyle(style + " -fx-background-radius: 10; -fx-padding: 3 8; -fx-font-size: 11px; -fx-font-weight: bold;");
        return chip;
    }

    private Label buildChip(String text, String bg, String fg) {
        Label chip = new Label(text);
        chip.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + fg + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 3 8;" +
            "-fx-font-size: 11px;"
        );
        return chip;
    }

    private Label buildEmptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 13px; -fx-padding: 12 0;");
        return lbl;
    }

    // --- Sorting ---

    private int compareTasks(TaskResponse t1, TaskResponse t2) {
        int p1 = getPriorityWeight(t1.getPriority()), p2 = getPriorityWeight(t2.getPriority());
        if (p1 != p2) return Integer.compare(p2, p1);
        if (t1.getDeadline() != null && t2.getDeadline() != null) {
            int cmp = t1.getDeadline().compareTo(t2.getDeadline());
            if (cmp != 0) return cmp;
        } else if (t1.getDeadline() != null) return -1;
        else if (t2.getDeadline() != null) return 1;
        return Double.compare(t2.getEstimatedHours(), t1.getEstimatedHours());
    }

    private int getPriorityWeight(String priority) {
        if (priority == null) return 2;
        return switch (priority.toUpperCase()) {
            case "CRITICAL" -> 4;
            case "HIGH"     -> 3;
            case "MEDIUM"   -> 2;
            case "LOW"      -> 1;
            default         -> 2;
        };
    }

    // --- Formatting ---

    private String formatDeadlineDateOnly(String isoDeadline) {
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(isoDeadline);
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.of("id", "ID"));
            String dayName = dt.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.of("id", "ID"));
            return dt.toLocalDate().format(fmt) + " (" + dayName + ")";
        } catch (Exception e) {
            try {
                if (isoDeadline != null && isoDeadline.length() >= 10) return isoDeadline.substring(0, 10);
            } catch (Exception ignored) {}
            return isoDeadline;
        }
    }

    private String formatCompletedTimestamp(String isoDateTime) {
        try {
            java.time.LocalDateTime dt = java.time.LocalDateTime.parse(isoDateTime);
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", java.util.Locale.of("id", "ID"));
            return dt.format(fmt);
        } catch (Exception e) {
            return isoDateTime;
        }
    }

    // --- Action Handlers ---

    private void handleUpdateStatus(TaskResponse task, String newStatus) {
        try {
            TaskService.updateTaskStatus(task.getId(), newStatus);
            loadData();
        } catch (Exception e) {
            showAlert("Pembaruan Gagal", "Gagal memperbarui status tugas: " + e.getMessage());
            loadData();
        }
    }

    private void handleEditTask(TaskResponse task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit_task.fxml"));
            Parent editTask = loader.load();
            EditTaskController controller = loader.getController();
            controller.setTask(task, this);

            javafx.stage.Stage stage = (javafx.stage.Stage) mainScrollPane.getScene().getWindow();
            stage.setScene(new Scene(editTask, stage.getWidth(), stage.getHeight()));
        } catch (Exception e) {
            showAlert("Kesalahan Sistem", "Gagal membuka halaman edit tugas: " + e.getMessage());
        }
    }

    private void handleDeleteTask(TaskResponse task) {
        boolean confirmed = com.taskly.frontend.util.AlertHelper.showConfirm(
                "Konfirmasi Penghapusan",
                "Apakah Anda yakin ingin menghapus tugas '" + task.getTitle() + "'?"
        );
        if (confirmed) {
            try {
                TaskService.deleteTask(task.getId());
                loadData();
            } catch (Exception e) {
                showAlert("Penghapusan Gagal", "Gagal menghapus tugas: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddTask() throws Exception {
        Parent addTask = FXMLLoader.load(getClass().getResource("/view/add_task.fxml"));
        javafx.stage.Stage stage = (javafx.stage.Stage) mainScrollPane.getScene().getWindow();
        stage.setScene(new Scene(addTask, stage.getWidth(), stage.getHeight()));
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    public void refreshData() {
        loadData();
    }

    @FXML
    private void handleLogout() throws Exception {
        AuthService.logout();
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        javafx.stage.Stage stage = (javafx.stage.Stage) mainScrollPane.getScene().getWindow();
        stage.setScene(new Scene(login, stage.getWidth(), stage.getHeight()));
    }

    private void showAlert(String title, String msg) {
        com.taskly.frontend.util.AlertHelper.showInfo(title, msg);
    }
}
