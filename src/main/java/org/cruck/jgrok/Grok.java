package org.cruck.jgrok;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author cruck
 */
public class Grok {
    private static final Logger log = Logger.getLogger(Grok.class.getCanonicalName());
    private final String original;
    private final Pattern regex;
    private final Map<String, String> variables;

    private Grok(String original, String pattern, Map<String, String> variables, int regexflags) {
        this.original = original;
        this.regex = Pattern.compile(pattern,regexflags);
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
            for (String key : variables.keySet()) {
                map.put(key, m.group(variables.get(key)));
            }

            return map;
        }
        return null;
    }
    /**
     * Static properties/methods
     */
    private final static ConcurrentHashMap<String, String> patterns = new ConcurrentHashMap<>();
    // (?<PATTERN>[A-Z]+)(?:\\:(?<VARNAME>[a-z_]+))?
    private final static Pattern GROK_SUBSTITUTION = Pattern.compile(".*?(?<WHOLE>%\\{(?<PATTERNNAME>[A-Z0-9_]+)(?::(?<KEYNAME>[a-z0-9_]+))?\\}).*", Pattern.MULTILINE & Pattern.DOTALL);

    public static void storePattern(String key, String pattern) {
        log.log(Level.FINE, "storing pattern {0} => {1}", new Object[]{key, pattern});
        patterns.put(key, pattern);
    }
    
    public static String getPattern(String key) {
        return patterns.get(key);
    }

    public static Grok compile(String grokpattern) {
        Map<String, String> variables = new HashMap<>();
        return compile(grokpattern, grokpattern, variables);
    }

    private static Grok compile(String original, String grokpattern, Map<String, String> variables) {
        log.log(Level.FINE, "compiling pattern {0} => {1}", new Object[]{original, grokpattern});
        Matcher m = GROK_SUBSTITUTION.matcher(Pattern.quote(grokpattern));
        if (m.matches()) {
            String whole = m.group("WHOLE");
            String patternname = m.group("PATTERNNAME");
            String varname = m.group("KEYNAME");
            String namedGroup = incrementString();
            if (varname != null) {
                variables.put(varname, namedGroup);
            }
            String prefix = varname != null ? "(?<" + namedGroup + ">" : "";
            String suffix = varname != null ? ")" : "";
            String pattern = patterns.get(patternname);
            if (pattern == null) {
                throw new IllegalArgumentException("Missing grok pattern " + patternname);
            }
            String replacement = prefix + pattern + suffix;
            String newpattern = grokpattern.replace(whole, replacement);
            return compile(original, newpattern, variables);
        }
        return new Grok(original, grokpattern, variables, Pattern.MULTILINE | Pattern.DOTALL);
    }
    private static final Random random = new Random();

    private static String lastString = ((char)('a'-1))+"";
    public static String incrementString() {
        char[] str = lastString.toCharArray();
        boolean allz = true;
        for(int i = 0; i < str.length; i++) {
            if(str[i]!='z') {
                allz=false;
                break;
            }
        }
        if(allz) {
            str = new char[str.length+1];
            Arrays.fill(str,'a');
        } else {
            for(int i = str.length-1; i>=0; i--) {
                if(str[i]=='z') {
                    str[i]='a';
                } else {
                    str[i]++;
                    break;
                }
            }
        }
        
        lastString = new String(str);
        return lastString;
    }

    public static void loadPatternDirectory(File dir) {
        for (File file : dir.listFiles()) {
            try {
                char[] buf = new char[(int) file.length() * 2];
                FileReader reader = new FileReader(file);
                int read = reader.read(buf);
                String str = new String(buf, 0, read);
                for (String line : str.split("\n")) {
                    String[] pattern = line.split(" ", 2);
                    Grok.storePattern(pattern[0], pattern[1]);
                }
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadBasicPatterns() {
        Grok.storePattern("SYSLOG", "<%{POSINT:syslog_pri}>%{SYSLOG_DATE:syslog_date} %{HOSTNAME:syslog_hostname} %{SYSLOG_PROG}:? %{GREEDYDATA:syslog_message}");
        Grok.storePattern("NOSPACE", "[^\\s-]+");
        Grok.storePattern("POSINT", "\\d+");
        Grok.storePattern("HOSTNAME", "\\b[A-Za-z0-9\\-\\.]+?\\b");
        Grok.storePattern("PROG", "(?:[\\w._/%-]+)");
        Grok.storePattern("SYSLOG_PROG", "%{PROG:syslog_program}(?:\\[%{POSINT:syslog_pid}\\]?)?");
        Grok.storePattern("DATA", ".*?");
        Grok.storePattern("GREEDYDATA", ".*");
        Grok.storePattern("MONTH", "\\b(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\b");
        Grok.storePattern("MONTHNUM", "(?:0?[1-9]|1[0-2])");
        Grok.storePattern("MONTHDAY", "(?:(?:0[1-9])|(?:[12][0-9])|(?:3[01])|[1-9])");
        Grok.storePattern("TIME", "(?!<[0-9])%{HOUR}:%{MINUTE}(?::%{SECOND})(?![0-9])");
        Grok.storePattern("HOUR", "(?:2[0123]|[01][0-9])");
        Grok.storePattern("MINUTE", "(?:[0-5][0-9])");
        Grok.storePattern("SECOND", "(?:(?:[0-5][0-9]|60)(?:[.,][0-9]+)?)");
        Grok.storePattern("SYSLOGTIMESTAMP", "%{MONTH} +%{MONTHDAY} %{TIME}");
        Grok.storePattern("SYSLOG_LPDATE", "%{MONTH}(  \\d| \\d\\d) \\d\\d:\\d\\d:\\d\\d");
        Grok.storePattern("RSYSLOG_ISO8601", "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+([\\+-]\\d{2}:\\d{2})?");
        Grok.storePattern("SYSLOG_DATE", "(%{SYSLOG_LPDATE}|%{RSYSLOG_ISO8601})");
    }
}
