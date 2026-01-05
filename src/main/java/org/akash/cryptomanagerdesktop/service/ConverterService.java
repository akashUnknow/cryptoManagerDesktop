package org.akash.cryptomanagerdesktop.service;

import java.nio.charset.StandardCharsets;

public class ConverterService {

    /**
     * Convert ASCII string to HEX format
     * @param ascii The ASCII string to convert
     * @return HEX representation with spaces between bytes
     */
    public String asciiToHex(String ascii) {
        String cleanHex = ascii.trim();
        if (ascii == null || cleanHex.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        byte[] bytes = cleanHex.getBytes(StandardCharsets.UTF_8);
        StringBuilder hex = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            hex.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) {
                hex.append("");
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
        String cleanHex = hex.replace(" ", "").trim();

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

    public String removeSpaces(String input) {
        return input.replace(" ", "").trim().toUpperCase();
    }

    public String swapValue(String input) {
        if (input == null) return "";
        input=input.trim().replaceAll("\\s+", "");
        if(input.length()%2!=0){
            input=input+"F";
        }
        StringBuilder swapped=new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i+=2) {
            swapped.append(input.charAt(i+1)).append(input.charAt(i));
        }
        return swapped.toString();
    }

    public String acc(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input is empty");
        }
        input=input.trim().replaceAll("\\s+", "");
        String regex="^809";
        if(input.length()==15){
            int lastDigit = Character.getNumericValue(input.charAt(input.length() - 1));
            int mathPro=(int) Math.pow(2,lastDigit);
            String accCal=Integer.toHexString(mathPro).toUpperCase();
            return accCal.length()<4?String.format("%4s", accCal).replace(' ', '0'):accCal;
        }
        if (input.length() == 18) {

            // swap pairs (equivalent to JS replace(/(.)(.)/g,"$2$1"))
            String swapped = swapPairs(input);

            if (swapped.matches(regex + ".*")) {

                String sliced = swapped.substring(3);

                int lastDigit = Character.getNumericValue(
                        sliced.charAt(sliced.length() - 1)
                );

                int mathPro = (int) Math.pow(2, lastDigit);
                String finalAcc = Integer.toHexString(mathPro).toUpperCase();

                return finalAcc.length() < 4
                        ? String.format("%4s", finalAcc).replace(' ', '0')
                        : finalAcc;
            }
        }
        throw new IllegalArgumentException("Invalid ACC input");
    }
    private String swapPairs(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i += 2) {
            sb.append(input.charAt(i + 1))
                    .append(input.charAt(i));
        }
        return sb.toString();
    }

}