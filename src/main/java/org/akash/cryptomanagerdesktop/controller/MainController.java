package org.akash.cryptomanagerdesktop.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.akash.cryptomanagerdesktop.service.CryptoService;
import org.akash.cryptomanagerdesktop.util.KeyResolver;

public class MainController {

    @FXML private ListView<String> algorithmList;
    @FXML private ComboBox<String> modeCombo;
    @FXML private ComboBox<String> paddingCombo;
    @FXML private ComboBox<String> inputTypeCombo;

    @FXML private TextField key1Field;
    @FXML private TextField key2Field;
    @FXML private TextField key3Field;
    @FXML private TextField ivField;
    @FXML private TextField tagLengthField;

    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;

    @FXML private Label statusLabel;
    @FXML private Label algorithmLabel;
    @FXML private Label keyLengthLabel;

    @FXML private Button encryptBtn;
    @FXML private Button decryptBtn;
    @FXML private Button copyBtn;
    @FXML private Button clearBtn;
    @FXML private Button sampleBtn;

    @FXML private VBox key2Container;
    @FXML private VBox key3Container;
    @FXML private VBox tagLengthContainer;

    private CryptoService cryptoService;
    private String currentAlgorithm = "DES";

    @FXML
    public void initialize() {
        cryptoService = new CryptoService();
        setupAlgorithmList();
        setupComboBoxes();
        setupEventHandlers();
        updateUIForAlgorithm("DES");
    }

    private void setupAlgorithmList() {
        algorithmList.getItems().addAll(
                "DES", "3DES (DESede)", "DESX", "AES-128", "AES-192", "AES-256",
                "RSA", "Blowfish", "RC2", "RC4"
        );
        algorithmList.getSelectionModel().select(0);
    }

    private void setupComboBoxes() {
        modeCombo.getItems().addAll("ECB", "CBC", "CFB", "OFB", "CTR", "GCM", "CCM");
        modeCombo.setValue("CBC");

        paddingCombo.getItems().addAll("PKCS5", "NoPadding", "ISO9797_M1", "ISO9797_M2");
        paddingCombo.setValue("PKCS5");

        inputTypeCombo.getItems().addAll("HEX", "Text");
        inputTypeCombo.setValue("HEX");
    }

    private void setupEventHandlers() {
        algorithmList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) updateUIForAlgorithm(newVal);
                }
        );

        modeCombo.valueProperty().addListener((obs, old, newVal) -> {
            updateIVRequirement(newVal);
            updatePaddingForMode(newVal);
        });

        key1Field.textProperty().addListener((obs, old, newVal) -> updateKeyLabel());
    }

    private void updateUIForAlgorithm(String algo) {
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
        loadSampleData();
        updateKeyLabel();
    }

    private String extractAlgorithm(String displayName) {
        if (displayName.equals("3DES (DESede)")) return "DESede";
        if (displayName.startsWith("AES")) return "AES";
        return displayName;
    }

    private void loadSampleData() {
        switch (currentAlgorithm) {
            case "DES":
                key1Field.setText("0123456789ABCDEF");
                inputArea.setText("1122334455667788");
                ivField.setText("0001020304050607");
                break;
            case "DESede":
                key1Field.setText("0123456789ABCDEF");
                key2Field.setText("0123456789ABCDEF");
                key3Field.setText("0123456789ABCDEF");
                inputArea.setText("1122334455667788");
                ivField.setText("0001020304050607");
                break;
            case "AES":
                key1Field.setText("0123456789ABCDEF0123456789ABCDEF");
                inputArea.setText("00112233445566778899AABBCCDDEEFF");
                ivField.setText("000102030405060708090A0B0C0D0E0F");
                break;
        }
    }

    private void updateKeyLabel() {
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

    private void updateIVRequirement(String mode) {
        boolean needsIV = !mode.equals("ECB");
        ivField.setDisable(!needsIV);

        boolean isAEAD = mode.equals("GCM") || mode.equals("CCM");
        tagLengthContainer.setVisible(isAEAD);
        tagLengthContainer.setManaged(isAEAD);
        if (isAEAD && tagLengthField.getText().isEmpty()) {
            tagLengthField.setText("128");
        }
    }

    private void updatePaddingForMode(String mode) {
        if (mode.equals("GCM") || mode.equals("CCM") ||
                mode.equals("CFB") || mode.equals("OFB") || mode.equals("CTR")) {
            paddingCombo.setValue("NoPadding");
            paddingCombo.setDisable(true);
        } else {
            paddingCombo.setDisable(false);
        }
    }

    @FXML
    private void handleEncrypt() {
        try {
            resolveKeyIfTextInput();
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
                showStatus("✓ Encryption successful", "success");
            } else {
                showStatus("✗ Error: " + result.getError(), "error");
            }
        } catch (Exception e) {
            showStatus("✗ Error: " + e.getMessage(), "error");
        }
    }

    @FXML
    private void handleDecrypt() {
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
                showStatus("✓ Decryption successful", "success");
            } else {
                showStatus("✗ Error: " + result.getError(), "error");
            }
        } catch (Exception e) {
            showStatus("✗ Error: " + e.getMessage(), "error");
        }
    }

    @FXML
    private void handleCopy() {
        if (!outputArea.getText().isEmpty()) {
            javafx.scene.input.Clipboard.getSystemClipboard().setContent(
                    new javafx.scene.input.ClipboardContent() {{
                        putString(outputArea.getText());
                    }}
            );
            showStatus("✓ Copied to clipboard", "success");
        }
    }

    @FXML
    private void handleClear() {
        inputArea.clear();
        outputArea.clear();
        showStatus("Cleared", "info");
    }

    @FXML
    private void handleLoadSample() {
        loadSampleData();
        showStatus("Sample data loaded", "info");
    }

    private String buildKeyHex() {
        String k1 = key1Field.getText().trim();
        if (currentAlgorithm.equals("DESede") || currentAlgorithm.equals("DESX") ) {
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

    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success", "error", "info");
        statusLabel.getStyleClass().add(type);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> statusLabel.setText("Ready"));
        pause.play();
    }
    private void resolveKeyIfTextInput() {
        if (!"Text".equals(inputTypeCombo.getValue())) return;

        String label = inputArea.getText().trim();
        if (label.isEmpty()) return;

        String hexKey = KeyResolver.resolveKey(label);
        key1Field.setText(hexKey);
    }

}