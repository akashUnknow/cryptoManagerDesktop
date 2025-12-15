package org.akash.cryptomanagerdesktop.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class KeyResolver {

    private static final Map<String, String> LABEL_TO_PATH = new HashMap<>();
    private static final Map<String, String> PATH_TO_KEY = new HashMap<>();

    static {
        loadExternal();
    }

    private static void loadExternal() {
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path configDir = baseDir.resolve("config");

        Path mapping = configDir.resolve("KeyMaping.txt");
        Path keymap  = configDir.resolve("Keymap.txt");

        try {
            if (Files.exists(mapping) && Files.exists(keymap)) {
                loadFile(mapping, LABEL_TO_PATH);
                loadFile(keymap, PATH_TO_KEY);
            } else {
                // fallback to bundled resources (DEV mode)
                loadFromClasspath("config/KeyMaping.txt", LABEL_TO_PATH);
                loadFromClasspath("config/Keymap.txt", PATH_TO_KEY);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed loading KeyMap configuration", e);
        }
    }
    private static void loadFromClasspath(String resource, Map<String, String> map)
            throws IOException {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        KeyResolver.class.getClassLoader()
                                .getResourceAsStream(resource),
                        StandardCharsets.UTF_8))) {

            if (br == null) {
                throw new FileNotFoundException("Missing classpath resource: " + resource);
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }



    private static void loadFile(Path file, Map<String, String> map)
            throws IOException {

        if (!Files.exists(file)) {
            throw new RuntimeException("Missing config file: " + file.toAbsolutePath());
        }

        try (BufferedReader br =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }

    public static String resolveKey(String label) {
        String path = LABEL_TO_PATH.get(label);
        if (path == null) {
            throw new IllegalArgumentException("Label not found: " + label);
        }

        String hexKey = PATH_TO_KEY.get(path);
        if (hexKey == null) {
            throw new IllegalArgumentException("Key path not found: " + path);
        }
        return hexKey;
    }
}
