package org.akash.cryptomanagerdesktop.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.akash.cryptomanagerdesktop.service.ConverterService;

public class ConverterController {

    private final ConverterService converterService;
    private final StatusController statusController;

    private ComboBox<String> conversionTypeCombo;
    private TextArea converterInputArea;
    private TextArea converterOutputArea;

    public ConverterController(StatusController statusController) {
        this.converterService = new ConverterService();
        this.statusController = statusController;
    }

    public void setFields(ComboBox<String> conversionTypeCombo,
                          TextArea converterInputArea,
                          TextArea converterOutputArea) {
        this.conversionTypeCombo = conversionTypeCombo;
        this.converterInputArea = converterInputArea;
        this.converterOutputArea = converterOutputArea;
    }

    public void setupConverterComboBox() {
        conversionTypeCombo.getItems().addAll(
                "ASCII to HEX",
                "HEX to ASCII","Space Remover","Swap Value","ACC"
        );
        conversionTypeCombo.setValue("ASCII to HEX");
    }

    public void handleConvert() {
        try {
            String input = converterInputArea.getText().trim();

            if (input.isEmpty()) {
                statusController.showStatus("✗ Input cannot be empty", "error");
                return;
            }

            String conversionType = conversionTypeCombo.getValue();
            String result;

//            if (conversionType.equals("ASCII to HEX")) {
//                result = converterService.asciiToHex(input);
//            } else {
//                result = converterService.hexToAscii(input);
//            }
            switch (conversionType){
                case "ASCII to HEX":
                    result=converterService.asciiToHex(input);
                    break;
                case "HEX to ASCII":
                    result=converterService.hexToAscii(input);
                    break;
                case "Space Remover":
                    result=converterService.removeSpaces(input);
                    break;
                case "Swap Value":
                    result=converterService.swapValue(input);
                    break;
                case "ACC":
                    result=converterService.acc(input);
                    break;
                default:
                    statusController.showStatus("✗ Unknown conversion type", "error");
                    return;
            }

            converterOutputArea.setText(result);
            statusController.showStatus("✓ Conversion successful", "success");

        } catch (Exception e) {
            statusController.showStatus("✗ Error: " + e.getMessage(), "error");
            converterOutputArea.setText("");
        }
    }

    public void handleCopyConverter() {
        if (!converterOutputArea.getText().isEmpty()) {
            javafx.scene.input.Clipboard.getSystemClipboard().setContent(
                    new javafx.scene.input.ClipboardContent() {{
                        putString(converterOutputArea.getText());
                    }}
            );
            statusController.showStatus("✓ Copied to clipboard", "success");
        }
    }
}