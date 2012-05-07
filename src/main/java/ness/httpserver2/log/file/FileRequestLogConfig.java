package ness.httpserver2.log.file;

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
        "timestamp,threadName,responseHeader:X-Trumpet-Track,remoteAddr,cookie:trumpet-json-api-authorization,method,requestUri,query,responseCode,responseHeader:Content-Length,elapsedTime";

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

