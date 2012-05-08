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
        "responseHeader:X-Trumpet-Track,remoteAddr,cookie:trumpet-json-api-authorization,method,requestUri,query,responseCode,responseHeader:Content-Length,elapsedTime";

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

    @Config("syslog_host")
    @Default("localhost")
    public String getSyslogHost()
    {
        return "localhost";
    }

    @Config("syslog_port")
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


    @Config("ianaIdentifier")
    @Default("12345")
    public int getIanaIdentifier()
    {
        return 12345;
    }

    @Config("maxMessageLength")
    @Default("1023")
    public int getMaxMessageLength()
    {
        return 1023;
    }

}


