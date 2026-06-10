package com.taskly.frontend.util;

import java.util.prefs.Preferences;

import javafx.stage.Stage;

public class StagePreferences {

    private static final Preferences PREFS = Preferences.userNodeForPackage(StagePreferences.class);

    private static final String KEY_W = "stageWidth";
    private static final String KEY_H = "stageHeight";
    private static final String KEY_MAX = "stageMaximized";

    private StagePreferences() {}

    public static void apply(Stage stage, double defaultWidth, double defaultHeight) {
        double w = PREFS.getDouble(KEY_W, defaultWidth);
        double h = PREFS.getDouble(KEY_H, defaultHeight);
        boolean max = PREFS.getBoolean(KEY_MAX, false);

        stage.setWidth(w);
        stage.setHeight(h);

        if (max) {
            stage.setMaximized(true);
        }
    }

    public static void persist(Stage stage) {
        try {
            PREFS.putDouble(KEY_W, stage.getWidth());
            PREFS.putDouble(KEY_H, stage.getHeight());
            PREFS.putBoolean(KEY_MAX, stage.isMaximized());
        } catch (Exception ignored) {
            // best-effort
        }
    }
}

