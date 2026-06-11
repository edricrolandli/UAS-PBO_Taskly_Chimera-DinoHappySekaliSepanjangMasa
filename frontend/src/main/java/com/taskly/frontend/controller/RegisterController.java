package com.taskly.frontend.controller;

import com.taskly.frontend.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        System.out.println("[DEBUG] handleRegister called");

        String fullName = fullNameField != null ? fullNameField.getText() : null;
        String email = emailField != null ? emailField.getText() : null;
        String password = passwordField != null ? passwordField.getText() : null;
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : null;

        if (fullName == null || fullName.trim().isEmpty()) {
            messageLabel.setText("Nama lengkap wajib diisi.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            messageLabel.setText("Alamat email wajib diisi.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            messageLabel.setText("Kata sandi wajib diisi.");
            return;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            messageLabel.setText("Konfirmasi kata sandi wajib diisi.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Konfirmasi kata sandi tidak cocok.");
            return;
        }

        try {
            System.out.println("[DEBUG] Register request start: fullName=" + fullName + ", email=" + email);

            AuthService.register(fullName, email, password, confirmPassword);

            System.out.println("[DEBUG] Register request success. Going to login.");

            com.taskly.frontend.util.AlertHelper.showInfo(
                "Pendaftaran Berhasil",
                "Akun Anda telah berhasil terdaftar! Klik OK untuk masuk ke halaman login."
            );

            goToLogin();
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            System.err.println("[ERROR] Register failed: " + errorMsg);

            // HttpClientUtil sudah print status & body. Di UI, tampilkan juga sebagian biar jelas.
            if (errorMsg != null && errorMsg.startsWith("HTTP")) {
                messageLabel.setText("Registrasi gagal: " + errorMsg);
            } else if (errorMsg != null && errorMsg.contains("400")) {
                messageLabel.setText("Registrasi gagal: Email sudah digunakan atau format data salah.");
            } else {
                messageLabel.setText("Gagal melakukan registrasi: " + (errorMsg != null ? errorMsg : "Terjadi kesalahan koneksi."));
            }
        }
    }

    @FXML
    private void goToLogin() throws Exception {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.setScene(new Scene(login, stage.getWidth(), stage.getHeight()));
    }

    @FXML
    private void initialize() {
        // Single form - no steps needed
    }
}

