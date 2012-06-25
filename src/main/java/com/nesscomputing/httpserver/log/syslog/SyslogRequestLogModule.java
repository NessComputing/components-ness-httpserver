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


import org.eclipse.jetty.server.handler.RequestLogHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.HttpServerHandlerBinder;

public class SyslogRequestLogModule extends AbstractModule
{
    private final Config config;

    public SyslogRequestLogModule(final Config config)
    {
        this.config = config;
    }

    @Override
    public void configure()
    {
        final SyslogRequestLogConfig syslogRequestLogConfig = config.getBean("ness.httpserver.request-log.syslog", SyslogRequestLogConfig.class);

        if (syslogRequestLogConfig.isEnabled()) {
            bind (SyslogRequestLogConfig.class).toInstance(syslogRequestLogConfig);
            bind(SyslogRequestLog.class).in(Scopes.SINGLETON);
            HttpServerHandlerBinder.bindLoggingHandler(binder()).toProvider(HandlerProvider.class);
        }
    }

    public static class HandlerProvider implements Provider<RequestLogHandler>
    {
        private final SyslogRequestLog syslogRequestLog;

        @Inject
        public HandlerProvider(final SyslogRequestLog syslogRequestLog)
        {
            this.syslogRequestLog = syslogRequestLog;
        }

        @Override
        public RequestLogHandler get()
        {
            final RequestLogHandler handler = new RequestLogHandler();
            handler.setRequestLog(syslogRequestLog);
            return handler;
        }
    }
}
