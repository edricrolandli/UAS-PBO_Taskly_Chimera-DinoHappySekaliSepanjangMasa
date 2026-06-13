package com.taskly.frontend.controller;

import com.taskly.frontend.model.TaskResponse;
import com.taskly.frontend.service.TaskService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditTaskController {
    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private DatePicker deadlinePicker;
    @FXML private Spinner<Integer> difficultySpinner;
    @FXML private TextField hoursField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Label messageLabel;

    private TaskResponse currentTask;

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("ASSIGNMENT", "EXAM", "GROUP");
        if (difficultySpinner.getValueFactory() == null) {
            difficultySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3));
        }
    }

    public void setTask(TaskResponse task, DashboardController dashboard) {
        this.currentTask = task;
        loadTaskData();
    }

    private void loadTaskData() {
        titleField.setText(currentTask.getTitle());
        descArea.setText(currentTask.getDescription() != null ? currentTask.getDescription() : "");

        if (currentTask.getDeadline() != null) {
            String dateStr = currentTask.getDeadline().substring(0, 10);
            deadlinePicker.setValue(LocalDate.parse(dateStr));
        }

        difficultySpinner.getValueFactory().setValue(currentTask.getDifficultyLevel());
        hoursField.setText(String.valueOf(currentTask.getEstimatedHours()));
        typeCombo.setValue(currentTask.getTaskType());
    }

    @FXML
    private void handleSave() {
        try {
            String title = titleField.getText();
            String desc = descArea.getText();
            LocalDate deadlineDate = deadlinePicker.getValue();

            if (title == null || title.trim().isEmpty()) {
                messageLabel.setText("Judul tugas wajib diisi.");
                return;
            }
            if (deadlineDate == null) {
                messageLabel.setText("Tanggal deadline wajib ditentukan.");
                return;
            }
            if (hoursField.getText() == null || hoursField.getText().trim().isEmpty()) {
                messageLabel.setText("Estimasi waktu wajib diisi.");
                return;
            }

            double hours;
            try {
                hours = Double.parseDouble(hoursField.getText().trim());
                if (hours <= 0) {
                    messageLabel.setText("Estimasi waktu harus lebih besar dari 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                messageLabel.setText("Format salah: estimasi waktu harus berupa angka (cth. 2.5).");
                return;
            }

            String deadlineStr = deadlineDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            int diff = difficultySpinner.getValue();
            String taskType = typeCombo.getValue();
            String status = currentTask.getStatus();

            TaskService.updateTask(currentTask.getId(), title, desc, deadlineStr, diff, hours, taskType, null, status);

            messageLabel.setStyle("-fx-text-fill: -color-success;");
            messageLabel.setText("✓ Perubahan berhasil disimpan! Mengalihkan ke dasbor...");

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            pause.setOnFinished(event -> {
                try {
                    handleCancel();
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to navigate to dashboard: " + e.getMessage());
                }
            });
            pause.play();
        } catch (Exception e) {
            messageLabel.setText("Gagal memperbarui tugas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() throws Exception {
        Parent dashboard = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.setScene(new Scene(dashboard, stage.getWidth(), stage.getHeight()));
    }
}
