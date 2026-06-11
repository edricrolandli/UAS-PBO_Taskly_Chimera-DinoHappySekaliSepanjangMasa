package com.taskly.frontend.controller;

import com.taskly.frontend.model.JwtResponse;
import com.taskly.frontend.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty()) {
            messageLabel.setText("Alamat email wajib diisi.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            messageLabel.setText("Kata sandi wajib diisi.");
            return;
        }

        try {
            AuthService.login(email, password);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("401") || errorMsg.contains("403"))) {
                messageLabel.setText("Gagal masuk: Email atau kata sandi salah. Silakan coba lagi.");
            } else {
                messageLabel.setText("Gagal masuk: Terjadi gangguan jaringan atau server sedang sibuk.");
            }
            e.printStackTrace();
            return;
        }

        messageLabel.setText("Masuk berhasil! Mengalihkan ke dasbor...");
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(dashboard, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Login berhasil, namun gagal memuat dasbor: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister() throws Exception {
        try {
            System.out.println("[DEBUG] goToRegister called");

            Parent register = FXMLLoader.load(getClass().getResource("/view/register.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(register, stage.getWidth(), stage.getHeight()));
            System.out.println("[DEBUG] register.fxml loaded + scene switched");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
