package org.akash.cryptomanagerdesktop.controller;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class StatusController {

    private Label statusLabel;

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void showStatus(String message, String type) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success", "error", "info");
        statusLabel.getStyleClass().add(type);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> statusLabel.setText("Ready"));
        pause.play();
    }
}