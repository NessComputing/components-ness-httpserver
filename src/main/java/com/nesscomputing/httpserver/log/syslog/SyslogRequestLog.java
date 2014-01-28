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

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.nesscomputing.galaxy.GalaxyConfig;
import com.nesscomputing.httpserver.log.LogFields;
import com.nesscomputing.httpserver.log.LogFields.LogField;
import com.nesscomputing.logging.Log;
import com.nesscomputing.syslog4j.Syslog;
import com.nesscomputing.syslog4j.SyslogConfigIF;
import com.nesscomputing.syslog4j.SyslogIF;
import com.nesscomputing.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;
import com.nesscomputing.syslog4j.impl.message.structured.StructuredSyslogMessage;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

/**
 * Send jetty request log straight into syslog to allow aggregation with logstash.
 */
@Singleton
public class SyslogRequestLog extends AbstractLifeCycle implements RequestLog
{
    private static final Log LOG = Log.findLog();

    private final Set<String> blackList;
    private final SyslogIF syslog;

    private final List<String> logFields;
    private final Map<String, LogField> knownFields;
    private final int ianaIdentifier;

    private GalaxyConfig galaxyConfig = null;

    @Inject
    public SyslogRequestLog(final SyslogRequestLogConfig requestLogConfig,
                            final Map<String, LogField> knownFields)
    {
        this.blackList = requestLogConfig.getBlacklist();
        final List<String> logFields = requestLogConfig.getLogFields();
        LogFields.validateLogFields(knownFields, logFields);
        this.logFields = logFields;
        this.knownFields = knownFields;
        this.ianaIdentifier = requestLogConfig.getIanaIdentifier();

        final SyslogIF syslog = Syslog.getInstance(requestLogConfig.getProtocol());
        if (syslog == null) {
            LOG.warn("No syslog instance for protocol '%s' available!", requestLogConfig.getProtocol());
        }
        else {
            final SyslogConfigIF syslogConfig = syslog.getConfig();
            syslogConfig.setUseStructuredData(true);
            syslogConfig.setTruncateMessage(true);
            syslogConfig.setMaxMessageLength(syslogConfig.getMaxMessageLength());
            syslogConfig.setCharSet(Charset.forName(requestLogConfig.getCharset()));

            syslogConfig.setFacility(requestLogConfig.getFacility());
            syslogConfig.setHost(requestLogConfig.getSyslogHost());
            syslogConfig.setPort(requestLogConfig.getSyslogPort());
            syslogConfig.setIdent("");
            syslogConfig.setLocalName(requestLogConfig.getHostname());

            final StructuredSyslogMessageProcessor messageProcessor = new StructuredSyslogMessageProcessor(requestLogConfig.getAppname());
            syslog.setStructuredMessageProcessor(messageProcessor);
        }
        this.syslog = syslog;
    }

    @Inject(optional=true)
    void setGalaxyConfig(final GalaxyConfig galaxyConfig)
    {
        this.galaxyConfig = galaxyConfig;
    }

    @Override
    public void doStop()
    {
        if (syslog != null) {
            syslog.flush();
        }
    }

    @Override
    public void log(final Request request, final Response response)
    {
        if (syslog == null) {
            return;
        }

        final String requestUri = request.getRequestURI();

        for (String blackListEntry : blackList) {
            if (StringUtils.startsWith(requestUri, blackListEntry)) {
                return;
            }
        }

        final String messageId = UUID.randomUUID().toString().replace("-", "");

        final Map<String, Builder<String, String>> builderMap = Maps.newHashMap();

        final Builder<String, String> logBuilder = ImmutableMap.builder();
        builderMap.put("l@" + ianaIdentifier, logBuilder);

        if (galaxyConfig != null) {
            if (galaxyConfig.getEnv().getAgentId() != null) {
                logBuilder.put("si", galaxyConfig.getEnv().getAgentId());
            }
            if (galaxyConfig.getDeploy().getConfig() != null) {
                logBuilder.put("sc", galaxyConfig.getDeploy().getConfig());
            }
        }

        for (Iterator<String> it = logFields.iterator(); it.hasNext(); ) {
            // Parse out fields that have parameters e.g. header:X-Trumpet-Track, and print
            final String[] chunks = StringUtils.split(it.next(), ":");

            final LogField field = knownFields.get(chunks[0]);
            if (chunks.length == 1) {
                final Object result = field.log(request, response, null);
                if (result != null) {
                    logBuilder.put(field.getShortName(), result.toString());
                }
            }
            else if (chunks.length == 2) {
                final String fieldName = field.getShortName() + "@" + ianaIdentifier;
                Builder<String, String> subBuilder = builderMap.get(fieldName);
                if (subBuilder == null) {
                    subBuilder = new Builder<String, String>();
                    builderMap.put(fieldName, subBuilder);
                }
                final String fieldKey = chunks[1].toLowerCase(Locale.ENGLISH).replace("=", "_");
                final Object result = field.log(request, response, chunks[1]);
                if (result != null) {
                    subBuilder.put(fieldKey, result.toString());
                }
            }
        }

        final String threadName = StringUtils.replaceChars(Thread.currentThread().getName()," \t", "");

        final StructuredSyslogMessage structuredMessage = new StructuredSyslogMessage(messageId,
                                                                                      threadName,
                                                                                      Maps.transformValues(builderMap, new Function<Builder<String, String>, Map<String, String>>() {
                                                                                          @Override
                                                                                          public Map<String, String> apply(final Builder<String, String> builder) {
                                                                                              return builder.build();
                                                                                          }
                                                                                      }),
                                                                                      null);
        syslog.info(structuredMessage);
    }
}
