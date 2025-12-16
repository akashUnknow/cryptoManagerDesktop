package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.*;
import org.akash.cryptomanagerdesktop.service.CryptoService;
import org.akash.cryptomanagerdesktop.util.KeyResolver;

public class CryptoController {

    private final CryptoService cryptoService;
    private final StatusController statusController;

    private TextArea inputArea;
    private TextArea outputArea;
    private ComboBox<String> inputTypeCombo;
    private ComboBox<String> modeCombo;
    private ComboBox<String> paddingCombo;
    private TextField key1Field;
    private TextField key2Field;
    private TextField key3Field;
    private TextField ivField;
    private TextField tagLengthField;
    private TextField keyLabel;

    private String currentAlgorithm = "DES";

    public CryptoController(StatusController statusController) {
        this.cryptoService = new CryptoService();
        this.statusController = statusController;
    }

    public void setFields(TextArea inputArea, TextArea outputArea,
                          ComboBox<String> inputTypeCombo, ComboBox<String> modeCombo,
                          ComboBox<String> paddingCombo, TextField key1Field,
                          TextField key2Field, TextField key3Field,
                          TextField ivField, TextField tagLengthField,
                          TextField keyLabel) {
        this.inputArea = inputArea;
        this.outputArea = outputArea;
        this.inputTypeCombo = inputTypeCombo;
        this.modeCombo = modeCombo;
        this.paddingCombo = paddingCombo;
        this.key1Field = key1Field;
        this.key2Field = key2Field;
        this.key3Field = key3Field;
        this.ivField = ivField;
        this.tagLengthField = tagLengthField;
        this.keyLabel = keyLabel;
    }

    public void setCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }

    public String getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public void handleEncrypt() {
        try {
            resolveKeyIfTextField();
            String keyHex = buildKeyHex();
            String ivHex = ivField.getText().trim();
            String data = inputArea.getText().trim();
            boolean isText = inputTypeCombo.getValue().equals("Text");
            Integer tagLen = getTagLength();

            CryptoService.CryptoResult result = cryptoService.encrypt(
                    currentAlgorithm, modeCombo.getValue(), paddingCombo.getValue(),
                    keyHex, ivHex, data, isText, tagLen
            );

            if (result.isSuccess()) {
                outputArea.setText(result.getResult());
                statusController.showStatus("✓ Encryption successful", "success");
            } else {
                statusController.showStatus("✗ Error: " + result.getError(), "error");
            }
        } catch (Exception e) {
            statusController.showStatus("✗ Error: " + e.getMessage(), "error");
        }
    }

    public void handleDecrypt() {
        try {
            String keyHex = buildKeyHex();
            String ivHex = ivField.getText().trim();
            String cipher = inputArea.getText().trim();
            boolean outputText = inputTypeCombo.getValue().equals("Text");
            Integer tagLen = getTagLength();

            CryptoService.CryptoResult result = cryptoService.decrypt(
                    currentAlgorithm, modeCombo.getValue(), paddingCombo.getValue(),
                    keyHex, ivHex, cipher, outputText, tagLen
            );

            if (result.isSuccess()) {
                outputArea.setText(result.getResult());
                statusController.showStatus("✓ Decryption successful", "success");
            } else {
                statusController.showStatus("✗ Error: " + result.getError(), "error");
            }
        } catch (Exception e) {
            statusController.showStatus("✗ Error: " + e.getMessage(), "error");
        }
    }

    public void handleCopy() {
        if (!outputArea.getText().isEmpty()) {
            javafx.scene.input.Clipboard.getSystemClipboard().setContent(
                    new javafx.scene.input.ClipboardContent() {{
                        putString(outputArea.getText());
                    }}
            );
            statusController.showStatus("✓ Copied to clipboard", "success");
        }
    }

    public void handleClear() {
        inputArea.clear();
        outputArea.clear();
        statusController.showStatus("Cleared", "info");
    }

    private String buildKeyHex() {
        String k1 = key1Field.getText().trim();
        if (currentAlgorithm.equals("DESede") || currentAlgorithm.equals("DESX")) {
            String k2 = key2Field.getText().trim();
            String k3 = key3Field.getText().trim();
            return k1 + k2 + k3;
        }
        return k1;
    }

    private Integer getTagLength() {
        String text = tagLengthField.getText().trim();
        return text.isEmpty() ? null : Integer.parseInt(text);
    }

    private void resolveKeyIfTextField() {
        if (keyLabel.getText().trim().isEmpty()) return;
        String label = keyLabel.getText().trim();
        String hexKey = KeyResolver.resolveKey(label);
        key1Field.setText(hexKey);
        key2Field.setText("");
        key3Field.setText("");
    }
}