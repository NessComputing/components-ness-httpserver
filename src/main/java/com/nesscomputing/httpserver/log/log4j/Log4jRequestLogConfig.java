/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.httpserver.log.log4j;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.skife.config.Config;
import org.skife.config.Default;

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

