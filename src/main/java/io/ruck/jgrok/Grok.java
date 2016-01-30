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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author cruck
 */
public class Grok {
    // (?<PATTERN>[A-Z]+)(?:\\:(?<VARNAME>[a-z_]+))?
    private static final Pattern GROK_SUBSTITUTION = Pattern.compile(".*?(?<WHOLE>%\\{(?<PATTERNNAME>[A-Z0-9_]+)(?::(?<KEYNAME>[a-z0-9_]+))?\\}).*", Pattern.MULTILINE & Pattern.DOTALL);
    private static final Logger log = LogManager.getLogger();
    private static String lastString = ((char) ('a' - 1)) + "";
    
    private final String original;
    private final Pattern regex;
    private final Map<String, String> variables;

    private Grok(String original, String pattern, Map<String, String> variables, int regexflags) {
        this.original = original;
        this.regex = Pattern.compile(pattern, regexflags);
        this.variables = Collections.unmodifiableMap(variables);
    }

    public String getPattern() {
        return regex.pattern();
    }

    public String getOriginalPattern() {
        return original;
    }

    public Map<String, String> parse(String line) {
        Map<String, String> map = new TreeMap<>();
        Matcher m = regex.matcher(line);
        if (m.matches()) {
            for (Entry<String,String> e : variables.entrySet()) {
                map.put(e.getValue(), m.group(e.getKey()));
            }

            return map;
        }
        return null;
    }

    public static Grok compile(String grokpattern, Patterns patterns) {
        Map<String, String> variables = new HashMap<>();
        return compile(grokpattern, grokpattern, variables, patterns);
    }

    private static Grok compile(String original, String grokpattern, Map<String, String> variables, Patterns patterns) {
        log.log(Level.INFO, "compiling pattern {} => {}", original, grokpattern);
        Matcher m = GROK_SUBSTITUTION.matcher(Pattern.quote(grokpattern));
        if (m.matches()) {
            String whole = m.group("WHOLE");
            String patternname = m.group("PATTERNNAME");
            String varname = m.group("KEYNAME");
            String namedGroup = incrementString();
            if (varname != null) {
                variables.put(namedGroup, varname);
            }
            String prefix = varname != null ? "(?<" + namedGroup + ">" : "";
            String suffix = varname != null ? ")" : "";
            String pattern = patterns.get(patternname);
            if (pattern == null) {
                throw new IllegalArgumentException("Missing grok pattern " + patternname);
            }
            String replacement = prefix + pattern + suffix;
            String newpattern = grokpattern.replace(whole, replacement);
            return compile(original, newpattern, variables, patterns);
        }
        return new Grok(original, grokpattern, variables, Pattern.MULTILINE | Pattern.DOTALL);
    }

    private static String incrementString() {
        char[] str = lastString.toCharArray();
        boolean allz = true;
        for (int i = 0; i < str.length; i++) {
            if (str[i] != 'z') {
                allz = false;
                break;
            }
        }
        if (allz) {
            str = new char[str.length + 1];
            Arrays.fill(str, 'a');
        } else {
            for (int i = str.length - 1; i >= 0; i--) {
                if (str[i] == 'z') {
                    str[i] = 'a';
                } else {
                    str[i]++;
                    break;
                }
            }
        }

        lastString = new String(str);
        return lastString;
    }
}
