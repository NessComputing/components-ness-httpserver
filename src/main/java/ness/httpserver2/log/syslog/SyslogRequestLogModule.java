package ness.httpserver2.log.syslog;

import ness.httpserver2.HttpServerHandlerBinder;

import org.eclipse.jetty.server.handler.RequestLogHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.nesscomputing.config.Config;

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
            HttpServerHandlerBinder.bindHandler(binder()).toProvider(HandlerProvider.class);
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
