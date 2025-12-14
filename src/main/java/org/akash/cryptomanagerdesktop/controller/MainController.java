package org.akash.cryptomanagerdesktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.akash.cryptomanagerdesktop.util.UiHelper;

/**
 * Main Controller - Orchestrates all sub-controllers
 * Following Single Responsibility Principle for better maintainability
 */
public class MainController {

    public Button converterBtn;
    // FXML UI Components - Crypto View
    @FXML private ListView<String> algorithmList;
    @FXML private ComboBox<String> modeCombo;
    @FXML private ComboBox<String> paddingCombo;
    @FXML private ComboBox<String> inputTypeCombo;
    @FXML private TextField key1Field;
    @FXML private TextField key2Field;
    @FXML private TextField key3Field;
    @FXML private TextField ivField;
    @FXML private TextField tagLengthField;
    @FXML private TextField keyLabel;
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Label statusLabel;
    @FXML private Label algorithmLabel;
    @FXML private Label keyLengthLabel;
    @FXML private VBox key2Container;
    @FXML private VBox key3Container;
    @FXML private VBox tagLengthContainer;

    // FXML UI Components - Converter View
    @FXML private ScrollPane cryptoView;
    @FXML private ScrollPane converterView;
    @FXML private ComboBox<String> conversionTypeCombo;
    @FXML private TextArea converterInputArea;
    @FXML private TextArea converterOutputArea;

    // Sub-Controllers
    private StatusController statusController;
    private CryptoController cryptoController;
    private ConverterController converterController;
    private UIStateController uiStateController;
    private ViewNavigationController viewNavigationController;

    private UiHelper uiHelper;

    @FXML
    public void initialize() {
        initializeControllers();
        setupUIComponents();
        setupEventHandlers();
        uiStateController.updateUIForAlgorithm("DES");
    }

    private void initializeControllers() {
        // Initialize in dependency order
        statusController = new StatusController();
        statusController.setStatusLabel(statusLabel);

        cryptoController = new CryptoController(statusController);
        cryptoController.setFields(inputArea, outputArea, inputTypeCombo, modeCombo,
                paddingCombo, key1Field, key2Field, key3Field, ivField,
                tagLengthField, keyLabel);

        converterController = new ConverterController(statusController);
        converterController.setFields(conversionTypeCombo, converterInputArea,
                converterOutputArea);

        uiStateController = new UIStateController(statusController);
        uiStateController.setFields(algorithmList, modeCombo, paddingCombo,
                algorithmLabel, keyLengthLabel, key2Container, key3Container,
                tagLengthContainer, key1Field, ivField, tagLengthField,
                inputArea, key2Field, key3Field);

        viewNavigationController = new ViewNavigationController(statusController);
        viewNavigationController.setFields(cryptoView, converterView, algorithmLabel);

        uiHelper = new UiHelper();
    }

    private void setupUIComponents() {
        uiStateController.setupAlgorithmList();
        uiHelper.setupComboBoxes(modeCombo, paddingCombo, inputTypeCombo);
        converterController.setupConverterComboBox();
    }

    private void setupEventHandlers() {
        // Algorithm selection
        algorithmList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        uiStateController.updateUIForAlgorithm(newVal);
                        cryptoController.setCurrentAlgorithm(uiStateController.getCurrentAlgorithm());
                        viewNavigationController.setCurrentAlgorithm(uiStateController.getCurrentAlgorithm());
                    }
                }
        );

        // Mode changes
        modeCombo.valueProperty().addListener((obs, old, newVal) -> {
            uiStateController.updateIVRequirement(newVal);
            uiStateController.updatePaddingForMode(newVal);
        });

        // Key input changes
        key1Field.textProperty().addListener((obs, old, newVal) ->
                uiStateController.updateKeyLabel());
    }

    // ============================================================
    // FXML Event Handlers - Crypto Operations
    // ============================================================

    @FXML
    private void handleEncrypt() {
        cryptoController.handleEncrypt();
    }

    @FXML
    private void handleDecrypt() {
        cryptoController.handleDecrypt();
    }

    @FXML
    private void handleCopy() {
        cryptoController.handleCopy();
    }

    @FXML
    private void handleClear() {
        cryptoController.handleClear();
    }

    @FXML
    private void handleLoadSample() {
        uiStateController.handleLoadSample();
    }

    // ============================================================
    // FXML Event Handlers - Converter Operations
    // ============================================================

    @FXML
    private void handleShowConverter() {
//        viewNavigationController.showConverterView();
        viewNavigationController.toggleView(converterBtn);
    }


    @FXML
    private void handleConvert() {
        converterController.handleConvert();
    }

    @FXML
    private void handleCopyConverter() {
        converterController.handleCopyConverter();
    }
}