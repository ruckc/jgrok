jgrok
=====
*Making complex regular expressions simplier through divide and conquer*

## Usage

Grok.storePattern("ISO8601","\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.|,)\\d+([\\+-]\\d{2}:?\\d{2})?");
Grok.storePattern("SYSLOG_PRI","\\d+");
Grok.storePattern("HOSTNAME","[A-Za-z0-9\\-\\.]+?");
Grok.storePattern("SYSLOG_PROGRAM","[^\\s]+");
Grok.storePattern("SYSLOG","<%{SYSLOG_PRI:syslog_pri}>%{ISO8601:syslog_date} %{HOSTNAME:syslog_hostname} %{SYSLOG_PROGRAM:syslog_program}: %{DATA:syslog_message}");

Grok syslog = Grok.compile("%{SYSLOG}");
Map<String,String> fields = syslog.parse("<78>2013-01-11T16:22:00.040610-05:00 localhost /usr/sbin/cron[41290]: (operator) CMD (/usr/libexec/save-entropy)");

## Building

```mvn clean install```

## Issues 

https://github.com/ruckc/jgrok/issues

