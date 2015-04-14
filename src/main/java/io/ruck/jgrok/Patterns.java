package io.ruck.jgrok;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ruckc
 */
public class Patterns {

    private final Map<String, String> patterns = new HashMap<>();

    public Patterns() {
    }
    
    public void put(String key, String pattern) {
        this.patterns.put(key, pattern);
    }

    public String get(String key) {
        return patterns.get(key);
    }

    public static Patterns load(InputStream is) throws IOException {
        Patterns patterns = new Patterns();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] pattern = line.split(" ",2);
                patterns.patterns.put(pattern[0], pattern[1]);
            }
        }
        return patterns;
    }

    public static Patterns load(Path path) throws IOException {
        return load(Files.newInputStream(path));
    }

    public static Patterns load(File path) throws IOException {
        return load(path.toPath());
    }
}
