package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.akash.cryptomanagerdesktop.util.Helper;

public class UIStateController {

    private final Helper helper;
    private final StatusController statusController;

    private ListView<String> algorithmList;
    private ComboBox<String> modeCombo;
    private ComboBox<String> paddingCombo;
    private Label algorithmLabel;
    private Label keyLengthLabel;
    private VBox key2Container;
    private VBox key3Container;
    private VBox tagLengthContainer;
    private TextField key1Field;
    private TextField ivField;
    private TextField tagLengthField;
    private TextArea inputArea;
    private TextField key2Field;
    private TextField key3Field;

    private String currentAlgorithm = "DES";

    public UIStateController(StatusController statusController) {
        this.helper = new Helper();
        this.statusController = statusController;
    }

    public void setFields(ListView<String> algorithmList, ComboBox<String> modeCombo,
                          ComboBox<String> paddingCombo, Label algorithmLabel,
                          Label keyLengthLabel, VBox key2Container, VBox key3Container,
                          VBox tagLengthContainer, TextField key1Field, TextField ivField,
                          TextField tagLengthField, TextArea inputArea,
                          TextField key2Field, TextField key3Field) {
        this.algorithmList = algorithmList;
        this.modeCombo = modeCombo;
        this.paddingCombo = paddingCombo;
        this.algorithmLabel = algorithmLabel;
        this.keyLengthLabel = keyLengthLabel;
        this.key2Container = key2Container;
        this.key3Container = key3Container;
        this.tagLengthContainer = tagLengthContainer;
        this.key1Field = key1Field;
        this.ivField = ivField;
        this.tagLengthField = tagLengthField;
        this.inputArea = inputArea;
        this.key2Field = key2Field;
        this.key3Field = key3Field;
    }

    public void setupAlgorithmList() {
        algorithmList.getItems().addAll(
                "DES", "3DES (DESede)", "AES-128", "AES-192", "AES-256","RSA"
        );
        algorithmList.getSelectionModel().select(0);
    }

    public void updateUIForAlgorithm(String algo) {
        currentAlgorithm = extractAlgorithm(algo);
        algorithmLabel.setText("Active: " + algo);

        // Update key fields visibility
        boolean is3DES = algo.equals("3DES (DESede)") || algo.equals("DESX");
        key2Container.setVisible(is3DES);
        key2Container.setManaged(is3DES);
        key3Container.setVisible(is3DES);
        key3Container.setManaged(is3DES);

        // Update modes for algorithm
        if (algo.startsWith("AES")) {
            if (!modeCombo.getItems().contains("GCM")) {
                modeCombo.getItems().addAll("GCM", "CCM");
            }
        } else {
            modeCombo.getItems().removeAll("GCM", "CCM");
        }

        // Set sample data
        helper.loadSampleData(currentAlgorithm, inputArea, key1Field, ivField, key2Field, key3Field);
        updateKeyLabel();
    }

    public void updateKeyLabel() {
        int len = key1Field.getText().length();
        if (currentAlgorithm.equals("AES")) {
            if (len == 32) keyLengthLabel.setText("AES-128 ✓");
            else if (len == 48) keyLengthLabel.setText("AES-192 ✓");
            else if (len == 64) keyLengthLabel.setText("AES-256 ✓");
            else keyLengthLabel.setText("Invalid length");
        } else if (currentAlgorithm.equals("DES")) {
            keyLengthLabel.setText(len == 16 ? "DES-56 ✓" : "Invalid");
        } else {
            keyLengthLabel.setText("");
        }
    }

    public void updateIVRequirement(String mode) {
        boolean needsIV = !mode.equals("ECB");
        ivField.setDisable(!needsIV);

        boolean isAEAD = mode.equals("GCM") || mode.equals("CCM");
        tagLengthContainer.setVisible(isAEAD);
        tagLengthContainer.setManaged(isAEAD);
        if (isAEAD && tagLengthField.getText().isEmpty()) {
            tagLengthField.setText("128");
        }
    }

    public void updatePaddingForMode(String mode) {
        if (mode.equals("GCM") || mode.equals("CCM") ||
                mode.equals("CFB") || mode.equals("OFB") || mode.equals("CTR")) {
            paddingCombo.setValue("NoPadding");
            paddingCombo.setDisable(true);
        } else {
            paddingCombo.setDisable(false);
        }
    }

    public void handleLoadSample() {
        helper.loadSampleData(currentAlgorithm, inputArea, key1Field, ivField, key2Field, key3Field);
        statusController.showStatus("Sample data loaded", "info");
    }

    public String getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    private String extractAlgorithm(String displayName) {
        if (displayName.equals("3DES (DESede)")) return "DESede";
        if (displayName.startsWith("AES")) return "AES";
        return displayName;
    }
}