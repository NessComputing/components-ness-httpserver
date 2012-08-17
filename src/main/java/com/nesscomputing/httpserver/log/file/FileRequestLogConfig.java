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
package com.nesscomputing.httpserver.log.file;

import java.util.List;
import java.util.Set;

import org.skife.config.Config;
import org.skife.config.Default;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class FileRequestLogConfig
{
    public static final String REQUEST_LOG_FIELDS_DEFAULT =
        "timestamp,threadName,responseHeader:X-Trumpet-Track,remoteAddr,requestHeader:Authorization,method,requestUri,query,responseCode,responseHeader:Content-Length,elapsedTime";

    @Config("blacklist")
    @Default("/selftest, /application.wadl")
    public Set<String> getBlacklist()
    {
        return ImmutableSet.of("/selftest", "/application.wadl");
    }

    @Config("enabled")
    @Default("false")
    public boolean isEnabled()
    {
        return false;
    }

    @Config("file")
    @Default("./access.log")
    public String getFileName()
    {
        return "./access.log";
    }

    @Config("fields")
    @Default(REQUEST_LOG_FIELDS_DEFAULT)
    public List<String> getLogFields() {
        return ImmutableList.copyOf(Splitter.on(",").split(REQUEST_LOG_FIELDS_DEFAULT));
    }
}

