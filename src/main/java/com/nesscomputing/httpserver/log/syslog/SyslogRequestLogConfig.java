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
package com.nesscomputing.httpserver.log.syslog;

import java.util.List;
import java.util.Set;

import org.skife.config.Config;
import org.skife.config.Default;
import org.skife.config.DefaultNull;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nesscomputing.syslog4j.SyslogFacility;

public abstract class SyslogRequestLogConfig
{
    public static final String REQUEST_LOG_FIELDS_DEFAULT =
        "responseHeader:X-Trumpet-Track,remoteAddr,responseHeader:Content-Length," +
        "requestHeader:Authorization,requestHeader:X-Ness-Server-Type,requestHeader:X-Ness-Server-Token," +
        "method,requestUri,query,responseCode,elapsedTime,serverInfoBinary,serverInfoVersion,serverInfoType,serverInfoToken";

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

    @Config("fields")
    @Default(REQUEST_LOG_FIELDS_DEFAULT)
    public List<String> getLogFields()
    {
        return ImmutableList.copyOf(Splitter.on(",").split(REQUEST_LOG_FIELDS_DEFAULT));
    }

    @Config("facility")
    @Default("local0")
    public SyslogFacility getFacility()
    {
        return SyslogFacility.local0;
    }

    @Config("protocol")
    @Default("udp")
    public String getProtocol()
    {
        return "udp";
    }

    @Config("syslog-host")
    @Default("localhost")
    public String getSyslogHost()
    {
        return "localhost";
    }

    @Config("syslog-port")
    @Default("514")
    public int getSyslogPort()
    {
        return 514;
    }

    @Config("charset")
    @Default("UTF-8")
    public String getCharset()
    {
        return "UTF-8";
    }

    @Config("hostname")
    @DefaultNull
    public String getHostname()
    {
        return null;
    }

    @Config("appname")
    @DefaultNull
    public String getAppname()
    {
        return null;
    }


    @Config("iana-identifier")
    @Default("12345")
    public int getIanaIdentifier()
    {
        return 12345;
    }

    @Config("max-msg-len")
    @Default("1023")
    public int getMaxMessageLength()
    {
        return 1023;
    }

}


