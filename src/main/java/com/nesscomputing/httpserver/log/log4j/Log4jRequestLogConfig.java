package com.nesscomputing.httpserver.log.log4j;

import java.util.Set;

import org.skife.config.Config;
import org.skife.config.Default;

import com.google.common.collect.ImmutableSet;

public abstract class Log4jRequestLogConfig
{
    private static final String LOG4J_REQUEST_LOG_PATTERN_DEFAULT =
        "Completed $method$ $requestUri$?$query$ with $responseCode$ in $elapsedTime$ms";

    @Config("enabled")
    @Default("false")
    public boolean isEnabled()
    {
        return false;
    }

    @Config("blacklist")
    @Default("/selftest, /application.wadl")
    public Set<String> getBlacklist()
    {
        return ImmutableSet.of("/selftest", "/application.wadl");
    }

    /** Note that this does NOT support the : selector syntax as the main request log does, at least for now */
    @Config("format")
    @Default(LOG4J_REQUEST_LOG_PATTERN_DEFAULT)
    public String getLogFields()
    {
        return LOG4J_REQUEST_LOG_PATTERN_DEFAULT;
    }

    @Config("logger-name")
    @Default("httpserver")
    public String getLoggerName()
    {
        return "httpserver";
    }
}

