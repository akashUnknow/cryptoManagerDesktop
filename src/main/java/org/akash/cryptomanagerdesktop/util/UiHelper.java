package org.akash.cryptomanagerdesktop.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UiHelper {
    private String currentAlgorithm = "DES";
    Helper helper=new Helper();
    public void setupComboBoxes(ComboBox<String> modeCombo,ComboBox<String> paddingCombo,ComboBox<String> inputTypeCombo) {
        modeCombo.getItems().addAll("ECB", "CBC", "CFB", "OFB", "CTR", "GCM", "CCM");
        modeCombo.setValue("CBC");

        paddingCombo.getItems().addAll("PKCS5", "NoPadding", "ISO9797_M1", "ISO9797_M2");
        paddingCombo.setValue("PKCS5");

        inputTypeCombo.getItems().addAll("HEX", "Text");
        inputTypeCombo.setValue("HEX");
    }

}
