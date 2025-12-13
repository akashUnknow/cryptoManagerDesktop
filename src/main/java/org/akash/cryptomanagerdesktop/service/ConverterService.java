package org.akash.cryptomanagerdesktop.service;

import java.nio.charset.StandardCharsets;

public class ConverterService {

    /**
     * Convert ASCII string to HEX format
     * @param ascii The ASCII string to convert
     * @return HEX representation with spaces between bytes
     */
    public String asciiToHex(String ascii) {
        if (ascii == null || ascii.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        byte[] bytes = ascii.getBytes(StandardCharsets.UTF_8);
        StringBuilder hex = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            hex.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) {
                hex.append(" ");
            }
        }

        return hex.toString();
    }

    /**
     * Convert HEX string to ASCII format
     * @param hex The HEX string to convert (with or without spaces)
     * @return ASCII representation
     */
    public String hexToAscii(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        // Remove spaces and validate
        String cleanHex = hex.replace(" ", "").trim().toUpperCase();

        if (!cleanHex.matches("^[0-9A-F]+$")) {
            throw new IllegalArgumentException("Invalid HEX string. Only 0-9 and A-F characters are allowed.");
        }

        if (cleanHex.length() % 2 != 0) {
            throw new IllegalArgumentException("HEX string must have even number of characters");
        }

        // Convert hex to bytes
        byte[] bytes = new byte[cleanHex.length() / 2];
        for (int i = 0; i < cleanHex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(cleanHex.charAt(i), 16) << 4)
                    + Character.digit(cleanHex.charAt(i + 1), 16));
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Validate if string is valid HEX
     * @param hex The string to validate
     * @return true if valid HEX, false otherwise
     */
    public boolean isValidHex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return false;
        }
        String clean = hex.replace(" ", "").trim();
        return clean.matches("^[0-9A-Fa-f]+$") && clean.length() % 2 == 0;
    }
}