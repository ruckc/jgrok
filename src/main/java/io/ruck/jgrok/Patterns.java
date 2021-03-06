/*
 * Copyright 2016 ruckc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    
    private final Map<String, Pattern> patternMap = new HashMap<>();
    
    public Patterns() {
    }
    
    public void putAll(Patterns patterns) {
        patternMap.putAll(patterns.patternMap);
    }
    
    public Pattern put(Pattern pattern) {
        return this.patternMap.put(pattern.getKey(), pattern);
    }
    
    public Pattern put(String key, String patternString) {
        Pattern pattern = new Pattern(key, patternString);
        this.patternMap.put(key, pattern);
        return pattern;
    }
    
    public Pattern getPattern(String key) {
        return patternMap.get(key);
    }
    
    public String get(String key) {
        Pattern pattern = patternMap.get(key);
        if (pattern == null) {
            throw new IllegalArgumentException(key + " does not have a grok pattern");
        }
        return pattern.getPattern();
    }
    
    public void remove(String key) {
        patternMap.remove(key);
    }
    
    public void remove(Pattern pattern) {
        patternMap.remove(pattern.getKey());
    }
    
    public static Patterns load(InputStream is) throws IOException {
        Patterns patterns = new Patterns();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] pattern = line.split(" ", 2);
                patterns.put(pattern[0], pattern[1]);
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
