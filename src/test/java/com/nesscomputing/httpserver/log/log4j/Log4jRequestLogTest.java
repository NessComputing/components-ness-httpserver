package com.nesscomputing.httpserver.log.log4j;

import static org.easymock.EasyMock.expect;

import javax.servlet.http.Cookie;


import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.log.LogFieldsModule;
import com.nesscomputing.httpserver.log.log4j.Log4jRequestLog;
import com.nesscomputing.httpserver.log.log4j.Log4jRequestLogModule;

public class Log4jRequestLogTest extends EasyMockSupport
{
    @Inject
    private Log4jRequestLog log = null;

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testLog4jLoggerWorks()
    {
        Request req = createNiceMock(Request.class);
        Response resp = createNiceMock(Response.class);

        expect(req.getRemoteAddr()).andReturn("1.2.3.4").anyTimes();
        Cookie[] cookies = {new Cookie("trumpet-JSON-api-AUTHORIZATION", "omgwtfbbq")};
        expect(req.getCookies()).andReturn(cookies).anyTimes();
        expect(req.getMethod()).andReturn("GET").anyTimes();
        expect(req.getRequestURL()).andReturn(new StringBuffer("foo")).anyTimes();
        expect(req.getQueryString()).andReturn("?bar").anyTimes();
        expect(req.getContentLength()).andReturn(42).anyTimes();
        expect(req.getTimeStamp()).andReturn(10000L).anyTimes();
        expect(resp.getStatus()).andReturn(201).anyTimes();

        replayAll();

        final Config config = Config.getFixedConfig("ness.httpserver.request-log.log4j.enabled", "true");
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new Log4jRequestLogModule(config));
        inj.injectMembers(this);

        Assert.assertNotNull(log);
        log.log(req, resp);

        verifyAll();
    }

    @Test
    public void testLog4jLoggerDisabledByDefault()
    {
        final Config config = Config.getEmptyConfig();
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new Log4jRequestLogModule(config));

        Assert.assertNull(inj.getExistingBinding(Key.get(Log4jRequestLog.class)));
    }

    private Module disableStuff()
    {
        return new Module() {
            @Override
            public void configure(final Binder binder) {
                binder.disableCircularProxies();
                binder.requireExplicitBindings();
            }
        };
    }
}
