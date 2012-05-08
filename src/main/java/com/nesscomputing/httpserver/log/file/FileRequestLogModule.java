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


import org.eclipse.jetty.server.handler.RequestLogHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.HttpServerHandlerBinder;

public class FileRequestLogModule extends AbstractModule
{
    private final Config config;

    public FileRequestLogModule(final Config config)
    {
        this.config = config;
    }

    @Override
    public void configure()
    {
        final FileRequestLogConfig fileRequestLogConfig = config.getBean("ness.httpserver.request-log.file", FileRequestLogConfig.class);

        if (fileRequestLogConfig.isEnabled()) {
            bind (FileRequestLogConfig.class).toInstance(fileRequestLogConfig);
            bind(FileRequestLog.class).in(Scopes.SINGLETON);
            HttpServerHandlerBinder.bindHandler(binder()).toProvider(HandlerProvider.class);
        }
    }

    public static class HandlerProvider implements Provider<RequestLogHandler>
    {
        private final FileRequestLog fileRequestLog;

        @Inject
        public HandlerProvider(final FileRequestLog fileRequestLog)
        {
            this.fileRequestLog = fileRequestLog;
        }

        @Override
        public RequestLogHandler get()
        {
            final RequestLogHandler handler = new RequestLogHandler();
            handler.setRequestLog(fileRequestLog);
            return handler;
        }
    }
}
