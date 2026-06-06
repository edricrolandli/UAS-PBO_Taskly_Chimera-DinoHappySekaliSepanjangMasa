package com.taskly.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import com.taskly.frontend.util.StagePreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TasklyApp extends Application {

    private static Process backendProcess;

    @Override
    public void init() throws Exception {
        startBackend();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Regular.ttf"), 12);
            javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"), 12);

            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            primaryStage.setTitle("Taskly - Task Management");
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(500);

            StagePreferences.apply(primaryStage, 1000, 700);

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            String stylesheet = getClass().getResource("/css/taskly.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);

            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setOnCloseRequest(e -> {
                StagePreferences.persist(primaryStage);
                stopBackend();
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal memuat aplikasi: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        stopBackend();
    }

    private void startBackend() throws Exception {
        Path backendJar = findBackendJar();
        if (backendJar == null) {
            System.out.println("backend.jar tidak ditemukan, asumsi backend sudah berjalan.");
            return;
        }

        // Gunakan java dari bundled runtime jpackage, fallback ke system java
        String javaCmd = findJavaExecutable();
        System.out.println("Menjalankan backend dari: " + backendJar);
        System.out.println("Java executable: " + javaCmd);

        ProcessBuilder pb = new ProcessBuilder(javaCmd, "-jar", backendJar.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        backendProcess = pb.start();

        // Buang output backend agar tidak blocking
        Thread logThread = new Thread(() -> {
            try (var reader = backendProcess.inputReader()) {
                reader.lines().forEach(line -> System.out.println("[backend] " + line));
            } catch (IOException ignored) {}
        });
        logThread.setDaemon(true);
        logThread.start();

        waitForBackend(30);
    }

    private String findJavaExecutable() {
        // Saat jpackage: runtime ada di $APPDIR/../runtime/bin/java.exe
        String appDir = System.getProperty("app.dir");
        if (appDir != null) {
            Path bundled = Path.of(appDir).getParent().resolve("runtime/bin/java.exe");
            if (Files.exists(bundled)) return bundled.toAbsolutePath().toString();
        }
        // Fallback: java dari PATH
        return ProcessHandle.current().info().command().orElse("java");
    }

    private Path findBackendJar() {
        // Saat berjalan via jpackage: $APPDIR di-set ke prop app.dir
        String appDir = System.getProperty("app.dir");
        if (appDir != null) {
            Path p = Path.of(appDir).resolve("backend.jar");
            if (Files.exists(p)) return p;
        }

        // Saat development: cari relatif dari working directory
        String[] candidates = {
            "backend.jar",
            "../backend/target/backend-1.0-SNAPSHOT.jar",
            "backend/target/backend-1.0-SNAPSHOT.jar"
        };
        for (String candidate : candidates) {
            Path p = Path.of(candidate);
            if (Files.exists(p)) return p;
        }

        return null;
    }

    private void waitForBackend(int timeoutSeconds) throws InterruptedException {
        System.out.println("Menunggu backend siap...");
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (!backendProcess.isAlive()) {
                System.err.println("Backend berhenti sebelum siap (exit code: " + backendProcess.exitValue() + ")");
                return;
            }
            try {
                HttpURLConnection conn = (HttpURLConnection)
                    new URL("http://localhost:8080/api/auth/health").openConnection();
                conn.setConnectTimeout(500);
                conn.connect();
                int code = conn.getResponseCode();
                if (code < 500) {
                    System.out.println("Backend siap!");
                    return;
                }
            } catch (IOException ignored) {}
            Thread.sleep(500);
        }
        System.out.println("Backend belum merespons setelah " + timeoutSeconds + "s, lanjut...");
    }

    private void stopBackend() {
        if (backendProcess != null && backendProcess.isAlive()) {
            System.out.println("Menghentikan backend...");
            backendProcess.descendants().forEach(ProcessHandle::destroy);
            backendProcess.destroy();
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
