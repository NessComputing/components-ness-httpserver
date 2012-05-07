package ness.httpserver2.log.log4j;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ness.httpserver2.log.LogFields.LogField;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nesscomputing.logging.Log;

/**
 * Log any request straight into log4j, which then can be used to redirect it e.g.
 * to graylog.
 */
@Singleton
public class Log4jRequestLog extends AbstractLifeCycle implements RequestLog
{
    private final Log log;
    private final String pattern;
    private final Set<String> blackList;
    private final Map<String, LogField> knownFields;


    @Inject
    public Log4jRequestLog(final Log4jRequestLogConfig config,
                           final Map<String, LogField> knownFields)
    {
        this.pattern = config.getLogFields();
        this.log = Log.forName(config.getLoggerName());
        this.blackList = config.getBlacklist();
        this.knownFields = knownFields;
    }

    @Override
    public void log(Request request, Response response) {
        final String requestUri = request.getRequestURI();

        for (String blackListEntry : blackList) {
            if (StringUtils.startsWith(requestUri, blackListEntry)) {
                return;
            }
        }

        final StringTemplate template = new StringTemplate(pattern);

        for (Entry<String, LogField> field : knownFields.entrySet()) {
            template.setAttribute(field.getKey(), ObjectUtils.toString(field.getValue().log(request, response, null)));
        }
        try {
            MDC.put("track", ObjectUtils.toString(response.getHeader("X-Trumpet-Track")));
            log.info(template.toString());
        } finally {
            MDC.remove("track");
        }
    }
}
