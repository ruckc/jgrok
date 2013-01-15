package org.cruck.jgrok;

import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author cruck
 */
public class Grok2Test {
    @Test
    public void testIncrementString() {
        Assert.assertEquals("a", Grok.incrementString());
        Assert.assertEquals("b", Grok.incrementString());
        for(int i = 0; i < 24; i++) { Grok.incrementString(); }
        Assert.assertEquals("aa", Grok.incrementString());
        for(int i = 0; i < 25; i++) { Grok.incrementString(); }
        Assert.assertEquals("ba", Grok.incrementString());
        for(int i = 0; i < 25*26-1; i++) { Grok.incrementString(); }
        Assert.assertEquals("aaa", Grok.incrementString());
    }
    
    @Test
    public void testSyslogGrok() {
        Grok.loadBasicPatterns();

        String line = "<31>Dec  5 13:32:21 dcgsbuilder-play rhnsd[25326]: running program /usr/sbin/rhn_check";

        Grok g = Grok.compile("%{SYSLOG}");
        Map<String, String> map = g.parse(line);
        System.out.println(line+"\n"+map);
        Assert.assertNotNull(map);
    }
    
    @Test
    public void testSyslogISO8601Grok() {
        Grok.loadBasicPatterns();
        String line = "<78>2012-12-05T18:55:00.329336-05:00 nas /usr/sbin/cron[3245]: (operator) CMD (/usr/libexec/save-entropy)";
        Grok g = Grok.compile("%{SYSLOG}");
        Map<String, String> map = g.parse(line);
        System.out.println(line+"\n"+map);
        Assert.assertNotNull(map);
    }
    
    
}
