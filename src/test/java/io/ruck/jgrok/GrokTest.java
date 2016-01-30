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

import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author cruck
 */
public class GrokTest {

    /**
     * Test of compile method, of class Grok.
     */
    @Test
    public void testCompile1() {
        Patterns patterns = new Patterns();
        patterns.put("WORD", "\\w+");
        String grokpattern = "Hello %{WORD:name}";
        Grok g = Grok.compile(grokpattern, patterns);
        Map<String, String> results = g.parse("Hello Curtis");
        Assert.assertNotNull(results);
        System.out.println(results);
        Assert.assertEquals("Curtis", results.get("name"));
    }

    @Test
    public void testCompile2() {
        Patterns patterns = new Patterns();
        patterns.put("WORD", "\\w+");
        patterns.put("TWONAMES", "%{WORD:name1} and %{WORD:name2}");
        String grokpattern = "Hello %{TWONAMES}";
        Grok g = Grok.compile(grokpattern, patterns);
        Map<String, String> results = g.parse("Hello Doug and Curtis");
        Assert.assertNotNull(results);
        System.out.println(results);
        Assert.assertEquals("Doug", results.get("name1"));
        Assert.assertEquals("Curtis", results.get("name2"));
    }

    @Test
    public void testCompile3() {
        Patterns patterns = new Patterns();
        patterns.put("WORD", "\\w+");
        patterns.put("TWONAMES", "%{WORD:name1} and %{WORD:name2}");
        patterns.put("THREENAMES", "%{WORD:name0}, %{TWONAMES}");
        String grokpattern = "Hello %{THREENAMES}";
        Grok g = Grok.compile(grokpattern, patterns);
        Map<String, String> results = g.parse("Hello Rob, Doug and Curtis");
        Assert.assertNotNull(results);
        System.out.println(results);
        Assert.assertEquals("Rob", results.get("name0"));
        Assert.assertEquals("Doug", results.get("name1"));
        Assert.assertEquals("Curtis", results.get("name2"));
    }

    @Test
    public void testCompile4() {
        HashSet<Pattern> patternSet = new HashSet<>();
        Patterns patterns = new Patterns();
        Pattern p = patterns.put("MISSING", ".*");
        Pattern p2 = patterns.getPattern("MISSING");
        Pattern p3 = patterns.put("MISSING", ".*");
        patternSet.add(p);
        patternSet.add(p2);
        patternSet.add(p3);
        assertEquals(1, patternSet.size());
        assertEquals(p, p2);
        assertTrue(p == p2);
        assertEquals(p, p3);
        assertFalse(p == p3);
        assertNotNull(patterns.get("MISSING"));
        patterns.remove("MISSING");
        try {
            patterns.get("MISSING");
            fail(); // not reachable
        } catch (IllegalArgumentException e) {
            // expected exception
        }
        patterns.put(p);
        assertNotNull(patterns.get("MISSING"));
        patterns.remove(p);
        try {
            Grok.compile("%{MISSING}", patterns);
            fail(); // not reachable
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
