package org.akash.cryptomanagerdesktop.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;

public class CryptoService {

    // Remove static block - provider is initialized in BouncyCastleLauncher

    public static class CryptoResult {
        private final String result;
        private final boolean success;
        private final String error;

        public CryptoResult(String result, boolean success, String error) {
            this.result = result;
            this.success = success;
            this.error = error;
        }

        public String getResult() { return result; }
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
    }

    public CryptoResult encrypt(String algorithm, String mode, String padding,
                                String keyHex, String ivHex, String dataHex,
                                boolean isTextInput, Integer tagLength) {
        try {
            // Verify BC provider is available
            Provider bcProvider = Security.getProvider("BC");
            if (bcProvider == null) {
                return new CryptoResult(null, false,
                        "BouncyCastle provider not available. Please restart the application.");
            }

            byte[] keyBytes = hexToBytes(keyHex);
            byte[] dataBytes = isTextInput ?
                    dataHex.getBytes(StandardCharsets.UTF_8) : hexToBytes(dataHex);

            String transformation = buildTransformation(algorithm, mode, padding);
            SecretKey key = createSecretKey(algorithm, keyBytes);

            // Use BC provider directly
            Cipher cipher = Cipher.getInstance(transformation, bcProvider);

            if (mode.equals("ECB")) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else if (mode.equals("GCM")) {
                byte[] ivBytes = hexToBytes(ivHex);
                int tagLen = tagLength != null ? tagLength : 128;
                GCMParameterSpec spec = new GCMParameterSpec(tagLen, ivBytes);
                cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            } else if (mode.equals("CCM")) {
                return new CryptoResult(null, false,
                        "CCM mode requires specialized library implementation");
            } else {
                byte[] ivBytes = hexToBytes(ivHex);
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            }

            byte[] cipherBytes = cipher.doFinal(dataBytes);
            String result = bytesToHex(cipherBytes);

            return new CryptoResult(result, true, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new CryptoResult(null, false, e.getMessage());
        }
    }

    public CryptoResult decrypt(String algorithm, String mode, String padding,
                                String keyHex, String ivHex, String cipherHex,
                                boolean outputAsText, Integer tagLength) {
        try {
            // Verify BC provider is available
            Provider bcProvider = Security.getProvider("BC");
            if (bcProvider == null) {
                return new CryptoResult(null, false,
                        "BouncyCastle provider not available. Please restart the application.");
            }

            byte[] keyBytes = hexToBytes(keyHex);
            byte[] cipherBytes = hexToBytes(cipherHex);

            String transformation = buildTransformation(algorithm, mode, padding);
            SecretKey key = createSecretKey(algorithm, keyBytes);

            // Use BC provider directly
            Cipher cipher = Cipher.getInstance(transformation, bcProvider);

            if (mode.equals("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else if (mode.equals("GCM")) {
                byte[] ivBytes = hexToBytes(ivHex);
                int tagLen = tagLength != null ? tagLength : 128;
                GCMParameterSpec spec = new GCMParameterSpec(tagLen, ivBytes);
                cipher.init(Cipher.DECRYPT_MODE, key, spec);
            } else if (mode.equals("CCM")) {
                return new CryptoResult(null, false,
                        "CCM mode requires specialized library implementation");
            } else {
                byte[] ivBytes = hexToBytes(ivHex);
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            }

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            String result = outputAsText ?
                    new String(plainBytes, StandardCharsets.UTF_8) : bytesToHex(plainBytes);

            return new CryptoResult(result, true, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new CryptoResult(null, false, e.getMessage());
        }
    }

    private String buildTransformation(String algo, String mode, String padding) {
        String paddingStr = mapPadding(padding);
        return algo + "/" + mode + "/" + paddingStr;
    }

    private SecretKey createSecretKey(String algorithm, byte[] keyBytes) {
        String keyAlgo = algorithm.equals("DESede") ? "DESede" : algorithm;
        return new SecretKeySpec(keyBytes, keyAlgo);
    }

    private String mapPadding(String padding) {
        switch (padding) {
            case "PKCS5": return "PKCS5Padding";
            case "ISO9797_M1": return "ISO9797M1Padding";
            case "ISO9797_M2": return "ISO9797M2Padding";
            case "NoPadding": return "NoPadding";
            default: return "PKCS5Padding";
        }
    }

    public static byte[] hexToBytes(String hex) {
        hex = hex.replace(" ", "").toUpperCase();
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}