package io.ruck.jgrok;

import java.util.Map;
import org.junit.Assert;
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
        Patterns patterns = new Patterns();
        try {
            Grok.compile("%{MISSING}", patterns);
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail();
    }
}
