package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import org.akash.cryptomanagerdesktop.model.ActiveView;

public class ViewNavigationController {


    private final StatusController statusController;
    private ScrollPane cryptoView;
    private ScrollPane converterView;
    private ScrollPane sofView;
    private Label algorithmLabel;
    private String currentAlgorithm = "DES";
    private ActiveView activeView= ActiveView.CRYPTO;

    public ViewNavigationController(StatusController statusController) {
        this.statusController = statusController;
    }

    public void setFields(ScrollPane cryptoView, ScrollPane converterView,ScrollPane sofView, Label algorithmLabel) {
        this.cryptoView = cryptoView;
        this.converterView = converterView;
        this.sofView = sofView;
        this.algorithmLabel = algorithmLabel;
    }

    public void setCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public void showConverterView() {
        cryptoView.setVisible(false);
        sofView.setVisible(false);
        converterView.setVisible(true);

        algorithmLabel.setText("Active: Converter");
        statusController.showStatus("Converter mode", "info");

        activeView = ActiveView.CONVERTER;
    }

    public void showCryptoView() {
        converterView.setVisible(false);
        sofView.setVisible(false);
        cryptoView.setVisible(true);
        algorithmLabel.setText("Active: " + currentAlgorithm);
        statusController.showStatus("Crypto mode", "info");
        activeView = ActiveView.CRYPTO;
    }

    public void showSofView() {
        converterView.setVisible(false);
        cryptoView.setVisible(false);
        sofView.setVisible(true);

        algorithmLabel.setText("Active: SOF Validator");
        statusController.showStatus("SOF Validator mode", "info");
        activeView = ActiveView.SOF;
    }
    public void toggleConverter() {
        if (activeView == ActiveView.CONVERTER) {
            showCryptoView();
        } else {
            showConverterView();
        }
    }
    public void toggleSof() {
        if (activeView == ActiveView.SOF) {
            showCryptoView();
        } else {
            showSofView();
        }
    }

}