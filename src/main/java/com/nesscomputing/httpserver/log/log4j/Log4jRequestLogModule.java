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


import org.eclipse.jetty.server.handler.RequestLogHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.HttpServerHandlerBinder;

public class Log4jRequestLogModule extends AbstractModule
{
    private final Config config;

    public Log4jRequestLogModule(final Config config)
    {
        this.config = config;
    }

    @Override
    public void configure()
    {
        final Log4jRequestLogConfig log4jRequestLogConfig = config.getBean("ness.httpserver.request-log.log4j", Log4jRequestLogConfig.class);

        if (log4jRequestLogConfig.isEnabled()) {
            bind (Log4jRequestLogConfig.class).toInstance(log4jRequestLogConfig);
            bind(Log4jRequestLog.class).in(Scopes.SINGLETON);
            HttpServerHandlerBinder.bindLoggingHandler(binder()).toProvider(HandlerProvider.class);
        }
    }

    public static class HandlerProvider implements Provider<RequestLogHandler>
    {
        private final Log4jRequestLog log4jRequestLog;

        @Inject
        public HandlerProvider(final Log4jRequestLog log4jRequestLog)
        {
            this.log4jRequestLog = log4jRequestLog;
        }

        @Override
        public RequestLogHandler get()
        {
            final RequestLogHandler handler = new RequestLogHandler();
            handler.setRequestLog(log4jRequestLog);
            return handler;
        }
    }
}
