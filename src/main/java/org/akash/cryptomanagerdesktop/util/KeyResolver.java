package org.akash.cryptomanagerdesktop.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class KeyResolver {
    private static final Map<String, String> LABEL_TO_PATH = new HashMap<>();
    private static final Map<String, String> PATH_TO_KEY = new HashMap<>();
    static {
        loadFile("KeyMap/KeyMaping.txt", LABEL_TO_PATH, true);
        loadFile("KeyMap/Keymap.txt", PATH_TO_KEY, false);
    }

    private static void loadFile(String resource,
                                 Map<String, String> map,
                                 boolean removeSemicolon) {
        try (InputStream is = KeyResolver.class
                .getClassLoader()
                .getResourceAsStream(resource)) {
            if (is == null) {
                throw new RuntimeException("Missing resource: " + resource);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line= br.readLine())!=null){
                line=line.trim();
                if (line.isEmpty() || line.startsWith("#"))   continue;
//                if (removeSemicolon && line.endsWith(";")){
//                    line=line.substring(0,line.length()-1);
//                }
                String[] parts=line.split("=",2);
                if(parts.length==2){
                    map.put(parts[0].trim(),parts[1].trim());
                }
            }
        }catch (Exception e) {
            throw new RuntimeException("Failed loading " + resource, e);
        }
    }
    public static String resolveKey(String label){
        String path=LABEL_TO_PATH.get(label);
        if(path==null){
            throw new IllegalArgumentException("Label not found: " + label);
        }
        String hexKey = PATH_TO_KEY.get(path);
        if (hexKey == null) {
            throw new IllegalArgumentException("Key path not found: " + path);
        }
        return hexKey;
    }
}
