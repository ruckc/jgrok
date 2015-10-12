package io.ruck.jgrok;

import java.util.Objects;

/**
 *
 * @author ruckc
 */
public class Pattern {
    private final String key;
    private final String pattern;

    public Pattern(String key, String pattern) {
        this.key = key;
        this.pattern = pattern;
    }

    public String getKey() {
        return key;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.key);
        hash = 19 * hash + Objects.hashCode(this.pattern);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pattern other = (Pattern) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return Objects.equals(this.pattern, other.pattern);
    }
}
