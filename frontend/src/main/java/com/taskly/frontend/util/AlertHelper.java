package com.taskly.frontend.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Optional;

public class AlertHelper {

    public static void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        configureAlert(alert, title, content);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        configureAlert(alert, title, content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void configureAlert(Alert alert, String title, String content) {
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null); // Remove default icon

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertHelper.class.getResource("/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        // Custom Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1c352d;");

        // Custom Content Label
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #3c3c3c;");

        // Custom Icon Label
        Label iconLabel = new Label();
        if (alert.getAlertType() == Alert.AlertType.CONFIRMATION) {
            iconLabel.setText("❓");
            iconLabel.setStyle("-fx-font-size: 26px; -fx-padding: 0 10 0 0;");
        } else {
            iconLabel.setText("ℹ️");
            iconLabel.setStyle("-fx-font-size: 26px; -fx-padding: 0 10 0 0;");
        }

        VBox textContainer = new VBox(titleLabel, contentLabel);
        textContainer.setSpacing(6);

        HBox body = new HBox(iconLabel, textContainer);
        body.setSpacing(10);
        body.setAlignment(Pos.CENTER_LEFT);

        dialogPane.setContent(body);

        // Customize standard buttons in DialogPane
        dialogPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setFill(Color.TRANSPARENT);
            }
        });

        // Some versions of JavaFX may already have a scene
        if (dialogPane.getScene() != null) {
            dialogPane.getScene().setFill(Color.TRANSPARENT);
        }

        // Apply style to the buttons
        for (ButtonType bt : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(bt);
            if (button != null) {
                button.getStyleClass().add("alert-button");
                if (bt == ButtonType.OK) {
                    button.setStyle("-fx-background-color: #a3d4c4; -fx-text-fill: #1c352d; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;");
                    button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #92c9b9; -fx-text-fill: #16322b; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;"));
                    button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #a3d4c4; -fx-text-fill: #1c352d; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;"));
                } else {
                    button.setStyle("-fx-background-color: #e8e3dc; -fx-text-fill: #3c3c3c; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;");
                    button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #d8cfc7; -fx-text-fill: #3c3c3c; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;"));
                    button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #e8e3dc; -fx-text-fill: #3c3c3c; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 20;"));
                }
            }
        }

        // Add micro-animation (fade-in and scale-in parallel transition)
        dialogPane.setOpacity(0.0);
        dialogPane.setScaleX(0.9);
        dialogPane.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(200), dialogPane);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), dialogPane);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition anim = new ParallelTransition(fade, scale);
        anim.play();
    }
}
