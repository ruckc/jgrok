package io.ruck.jgrok;

import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author cruck
 */
public class Grok2Test {
    //@Test
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
    
    @Test
    public void testSyslog1() {
        Grok.storePattern("GREEDYDATA",".*");
        Grok.storePattern("POSINT","\\d+");
        Grok.storePattern("PROG","(?:[\\w._/%-]+)");
        Grok.storePattern("SYSLOG_PROG","(?:%{PROG:syslog_program}(?:\\[%{POSINT:syslog_pid}\\]?)?)");
        Grok.storePattern("HOSTNAME","[A-Za-z0-9\\-\\.]+");
        Grok.storePattern("RSYSLOG_PLUS_ISO8601","\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.|,)\\d+(?:[\\+-]\\d{2}:?\\d{2})?");
        Grok.storePattern("SYSLOG_DATE", "%{RSYSLOG_PLUS_ISO8601}");
        Grok.storePattern("TEST1", "^<%{POSINT:syslog_pri}>%{SYSLOG_DATE:syslog_date} %{HOSTNAME:syslog_hostname} %{SYSLOG_PROG}:? %{GREEDYDATA:syslog_message}$");
        String line = "<14>2013-10-11T00:00:01.750529+00:00 fuk-ge audispd: node=fuk-ge.unclass2.iesil type=USER_START msg=audit(1381449601.749:33603): user pid=32070 uid=0 auid=0 ses=412 subj=system_u:system_r:crond_t:s0-s0:c0.c1023 msg='op=PAM:session_open acct=\"root\" exe=\"/usr/sbin/crond\" hostname=? addr=? terminal=cron res=success'";
        Grok g = Grok.compile("%{TEST1}");
        System.out.println(g.getPattern());
        Map<String, String> map = g.parse(line);
        System.out.println(line+"\n"+map);
        Assert.assertNotNull(map);
        Assert.assertEquals(6, map.size());
    }
}
