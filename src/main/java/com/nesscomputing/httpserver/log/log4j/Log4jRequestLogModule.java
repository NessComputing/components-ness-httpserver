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
            HttpServerHandlerBinder.bindHandler(binder()).toProvider(HandlerProvider.class);
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
