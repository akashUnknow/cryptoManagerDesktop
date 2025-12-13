package org.akash.cryptomanagerdesktop.util;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Helper {

    public void loadSampleData(
            String currentAlgorithm,
            TextArea inputArea,
            TextField key1Field,
            TextField ivField,
            TextField key2Field,
            TextField key3Field
    ) {

        // clear old values
        key1Field.clear();
        key2Field.clear();
        key3Field.clear();
        ivField.clear();
        inputArea.clear();

        switch (currentAlgorithm) {

            case "DES":
                key1Field.setText("0123456789ABCDEF");
                ivField.setText("0001020304050607");
                inputArea.setText("1122334455667788");
                break;

            case "DESede": // 3DES
                key1Field.setText("0123456789ABCDEF");
                key2Field.setText("23456789ABCDEF01");
                key3Field.setText("456789ABCDEF0123");
                ivField.setText("0001020304050607");
                inputArea.setText("1122334455667788");
                break;


            case "AES":
                key1Field.setText("00112233445566778899AABBCCDDEEFF");
                ivField.setText("000102030405060708090A0B0C0D0E0F");
                inputArea.setText("11223344556677889900AABBCCDDEEFF");
                break;

            default:
                // safe fallback
                inputArea.setText("1122334455667788");
        }
    }
}
