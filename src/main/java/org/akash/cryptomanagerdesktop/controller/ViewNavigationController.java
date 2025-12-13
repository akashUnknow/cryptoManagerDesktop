package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class ViewNavigationController {

    private final StatusController statusController;
    private ScrollPane cryptoView;
    private ScrollPane converterView;
    private Label algorithmLabel;
    private String currentAlgorithm = "DES";

    public ViewNavigationController(StatusController statusController) {
        this.statusController = statusController;
    }

    public void setFields(ScrollPane cryptoView, ScrollPane converterView, Label algorithmLabel) {
        this.cryptoView = cryptoView;
        this.converterView = converterView;
        this.algorithmLabel = algorithmLabel;
    }

    public void setCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public void showConverterView() {
        cryptoView.setVisible(false);
        converterView.setVisible(true);
        algorithmLabel.setText("Active: Converter");
        statusController.showStatus("Converter mode", "info");
    }

    public void showCryptoView() {
        converterView.setVisible(false);
        cryptoView.setVisible(true);
        algorithmLabel.setText("Active: " + currentAlgorithm);
        statusController.showStatus("Crypto mode", "info");
    }
}