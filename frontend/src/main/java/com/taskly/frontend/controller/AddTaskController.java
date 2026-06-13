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

public class AddTaskController {
    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private DatePicker deadlinePicker;
    @FXML private Spinner<Integer> difficultySpinner;
    @FXML private TextField hoursField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("ASSIGNMENT", "EXAM", "GROUP");
        typeCombo.setValue("ASSIGNMENT");

        // Inisialisasi pengaman nilai Spinner jika belum ter-set di FXML
        if (difficultySpinner.getValueFactory() == null) {
            difficultySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3));
        }
    }

    @FXML
    private void handleSave() {
        try {
            String title = titleField.getText();
            String desc = descArea.getText();
            LocalDate deadlineDate = deadlinePicker.getValue();

            if (title == null || title.trim().isEmpty()) {
                messageLabel.setText("Judul tugas wajib diisi. Silakan masukkan nama tugas.");
                return;
            }
            if (deadlineDate == null) {
                messageLabel.setText("Tanggal batas waktu (deadline) wajib ditentukan.");
                return;
            }
            if (hoursField.getText() == null || hoursField.getText().trim().isEmpty()) {
                messageLabel.setText("Estimasi waktu pengerjaan wajib diisi.");
                return;
            }

            double hours;
            try {
                hours = Double.parseDouble(hoursField.getText().trim());
                if (hours <= 0) {
                    messageLabel.setText("Estimasi waktu pengerjaan harus lebih besar dari 0 jam.");
                    return;
                }
            } catch (NumberFormatException e) {
                messageLabel.setText("Format salah: Estimasi waktu harus berupa angka (contoh: 2.5).");
                return;
            }

            // Format penulisan tanggal disesuaikan secara presisi dengan kebutuhan ISO JIT Compiler
            String deadlineStr = deadlineDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            int diff = difficultySpinner.getValue();
            String taskType = typeCombo.getValue();

            System.out.println("[DEBUG] Creating task: title=" + title + ", deadline=" + deadlineStr + ", type=" + taskType);
            
            // Eksekusi penembakan API data ke Backend
            TaskService.createTask(title, desc, deadlineStr, diff, hours, taskType);

            messageLabel.setText("✓ Tugas baru berhasil ditambahkan! Mengalihkan ke dasbor...");
            System.out.println("[DEBUG] Task created successfully");
            
            // Berpindah kembali ke Dashboard utama secara otomatis setelah sukses menggunakan PauseTransition
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
            String errorMsg = "Gagal menyimpan tugas: " + e.getMessage();
            messageLabel.setText(errorMsg);
            System.err.println("[ERROR] " + errorMsg);
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
