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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author cruck
 */
public class Grok2Test {
    @Test
    public void testSyslog1() {
        Patterns patterns = new Patterns();
        patterns.put("GREEDYDATA", ".*");
        patterns.put("POSINT", "\\d+");
        patterns.put("PROG", "(?:[\\w._/%-]+)");
        patterns.put("SYSLOG_PROG", "(?:%{PROG:syslog_program}(?:\\[%{POSINT:syslog_pid}\\]?)?)");
        patterns.put("HOSTNAME", "[A-Za-z0-9\\-\\.]+");
        patterns.put("RSYSLOG_PLUS_ISO8601", "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.|,)\\d+(?:[\\+-]\\d{2}:?\\d{2})?");
        patterns.put("SYSLOG_DATE", "%{RSYSLOG_PLUS_ISO8601}");
        patterns.put("TEST1", "^<%{POSINT:syslog_pri}>%{SYSLOG_DATE:syslog_date} %{HOSTNAME:syslog_hostname} %{SYSLOG_PROG}:? %{GREEDYDATA:syslog_message}$");
        String line = "<14>2013-10-11T00:00:01.750529+00:00 fuk-ge audispd: node=fuk-ge.unclass2.iesil type=USER_START msg=audit(1381449601.749:33603): user pid=32070 uid=0 auid=0 ses=412 subj=system_u:system_r:crond_t:s0-s0:c0.c1023 msg='op=PAM:session_open acct=\"root\" exe=\"/usr/sbin/crond\" hostname=? addr=? terminal=cron res=success'";
        Grok g = Grok.compile("%{TEST1}", patterns);
        System.out.println(g.getPattern());
        Map<String, String> map = g.parse(line);
        System.out.println(line + "\n" + map);
        assertNotNull(map);
        assertEquals(6, map.size());
    }
    
    @Test
    public void testPatterns() throws IOException {
        String pattern = Patterns.load(Paths.get("src/test/resources/sample.patterns").toFile()).get("ALPHAOMEGA");
        assertNotNull(pattern);
    }
}
