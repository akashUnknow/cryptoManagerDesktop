package org.akash.cryptomanagerdesktop.service;

import java.util.ArrayList;
import java.util.List;

public class ValidationService {

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public String getErrorMessage() {
            return String.join("\n", errors);
        }
    }

    public ValidationResult validateEncryptionRequest(
            String algorithm, String mode, String padding,
            String key, String iv, String data,
            boolean isTextInput, Integer tagLength) {

        List<String> errors = new ArrayList<>();

        // Validate algorithm
        if (algorithm == null || algorithm.isEmpty()) {
            errors.add("Algorithm is required");
        }

        // Validate key
        if (key == null || key.isEmpty()) {
            errors.add("Key is required");
        } else if (!isValidHex(key)) {
            errors.add("Key must be valid hexadecimal");
        } else {
            validateKeyLength(algorithm, key, errors);
        }

        // Validate IV
        if (!mode.equals("ECB")) {
            if (iv == null || iv.isEmpty()) {
                errors.add("IV/Nonce is required for " + mode + " mode");
            } else if (!isValidHex(iv)) {
                errors.add("IV must be valid hexadecimal");
            } else {
                validateIVLength(algorithm, mode, iv, errors);
            }
        }

        // Validate data
        if (data == null || data.isEmpty()) {
            errors.add("Input data cannot be empty");
        } else if (!isTextInput && !isValidHex(data)) {
            errors.add("HEX data contains invalid characters");
        } else if (!isTextInput && data.length() % 2 != 0) {
            errors.add("HEX data must have even number of characters");
        }

        // Validate padding for specific modes
        validatePadding(mode, padding, errors);

        // Validate tag length for AEAD modes
        if (mode.equals("GCM") || mode.equals("CCM")) {
            if (tagLength == null) {
                errors.add(mode + " mode requires tag length");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private void validateKeyLength(String algorithm, String key, List<String> errors) {
        int keyLength = key.length();

        switch (algorithm) {
            case "DES":
                if (keyLength != 16) {
                    errors.add("DES key must be exactly 16 hex chars (8 bytes)");
                }
                break;
            case "DESede":
                if (keyLength != 32 && keyLength != 48) {
                    errors.add("3DES key must be 32 hex chars (2-key) or 48 hex chars (3-key)");
                }
                break;
            case "DESX":
                if (keyLength != 48) {
                    errors.add("DESX key must be exactly 48 hex chars");
                }
                break;
            case "AES":
                int keyBytes = keyLength / 2;
                if (keyBytes != 16 && keyBytes != 24 && keyBytes != 32) {
                    errors.add(String.format(
                            "AES key must be 32 hex chars (AES-128), 48 hex chars (AES-192), " +
                                    "or 64 hex chars (AES-256). Current length: %d", keyLength
                    ));
                }
                break;
        }
    }

    private void validateIVLength(String algorithm, String mode, String iv, List<String> errors) {
        int ivLength = iv.length();

        if (algorithm.equals("AES")) {
            if (mode.equals("GCM")) {
                if (ivLength != 24 && ivLength != 32) {
                    errors.add("GCM nonce must be 24 hex chars (12 bytes) or 32 hex chars (16 bytes)");
                }
            } else if (mode.equals("CCM")) {
                if (ivLength < 14 || ivLength > 26) {
                    errors.add("CCM nonce must be between 14-26 hex chars (7-13 bytes)");
                }
            } else {
                if (ivLength != 32) {
                    errors.add("AES IV must be 32 hex chars (16 bytes)");
                }
            }
        } else {
            // DES/3DES
            if (ivLength != 16) {
                errors.add("DES/3DES IV must be 16 hex chars (8 bytes)");
            }
        }
    }

    private void validatePadding(String mode, String padding, List<String> errors) {
        List<String> noPaddingModes = List.of("CFB", "OFB", "CTR", "GCM", "CCM");

        if (noPaddingModes.contains(mode) && !padding.equals("NoPadding")) {
            errors.add(mode + " mode must use NoPadding");
        }
    }

    public static boolean isValidHex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return false;
        }
        String clean = hex.replace(" ", "").toUpperCase();
        return clean.matches("^[0-9A-F]+$");
    }
}