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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;

import java.util.List;

import javax.servlet.http.Cookie;


import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.galaxy.GalaxyConfig;
import com.nesscomputing.httpserver.log.LogFieldsModule;
import com.nesscomputing.httpserver.log.syslog.SyslogRequestLog;
import com.nesscomputing.httpserver.log.syslog.SyslogRequestLogModule;
import com.nesscomputing.syslog4j.Syslog;
import com.nesscomputing.syslog4j.SyslogIF;
import com.nesscomputing.syslog4j.SyslogLevel;
import com.nesscomputing.syslog4j.SyslogRuntimeException;
import com.nesscomputing.syslog4j.impl.AbstractSyslogWriter;
import com.nesscomputing.syslog4j.impl.net.AbstractNetSyslog;
import com.nesscomputing.syslog4j.impl.net.AbstractNetSyslogConfig;
import com.nesscomputing.testing.lessio.AllowNetworkListen;

@AllowNetworkListen(ports={0})
public class SyslogRequestLogTest extends EasyMockSupport
{

    @Inject
    private SyslogRequestLog syslogRequestLog;

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testInvalidLogPatternBadSpecifier() {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.syslog.enabled", "true",
                                                    "ness.httpserver.request-log.syslog.fields", "remoteAddr,foo");

        try {
            final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new SyslogRequestLogModule(config));
            inj.injectMembers(this);
            fail();
        }
        catch (CreationException ce) {
            Assert.assertEquals(IllegalArgumentException.class, ce.getCause().getClass());
            Assert.assertEquals("Invalid log pattern: unknown field <foo>", ce.getCause().getMessage());
        }
    }

    @Test
    public void testInvalidLogPatternTooManyColons() {
        try {
            final Config config = Config.getFixedConfig("ness.httpserver.request-log.syslog.enabled", "true",
                                                        "ness.httpserver.request-log.syslog.fields", "remoteAddr,cookie:no-here:bar");
            final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new SyslogRequestLogModule(config));
            inj.injectMembers(this);
            fail();
        }
        catch (CreationException ce) {
            Assert.assertEquals(IllegalArgumentException.class, ce.getCause().getClass());
            Assert.assertEquals("Bad specifier \"cookie:no-here:bar\" has too many colons", ce.getCause().getMessage());
        }
    }

    @Test
    public void testDefaultConfigWorks()
    {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.syslog.enabled", "true");
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new SyslogRequestLogModule(config));
        inj.injectMembers(this);
        Assert.assertNotNull(syslogRequestLog);
    }

    @Test
    public void testDisabledByDefault()
    {
        final Config config = Config.getEmptyConfig();
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new SyslogRequestLogModule(config));

        Assert.assertNull(inj.getExistingBinding(Key.get(SyslogRequestLog.class)));
    }

    @Test
    public void testMockRequestWithNullGalaxy()
    {
        testMockRequest(new Module() {
            @Override
            public void configure(Binder binder) {
                // empty
            }
        });
    }

    @Test
    public void testMockRequestWithEmptyGalaxyConfig()
    {
        testMockRequest(new AbstractModule() {
            @Override
            protected void configure() {
                bind(GalaxyConfig.class);
            }
        });
    }

    public void testMockRequest(Module extra)
    {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.syslog.enabled", "true",
                                                    "ness.httpserver.request-log.syslog.protocol", "fake");

        Syslog.createInstance("fake", new AbstractNetSyslogConfig() {

            @Override
            public Class<? extends SyslogIF> getSyslogClass() {
                return FakeSyslog.class;
            }
        });

        final FakeSyslog fakeSyslog = FakeSyslog.class.cast(Syslog.getInstance("fake"));
        Assert.assertNotNull(fakeSyslog);

        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new SyslogRequestLogModule(config), new ConfigModule(config), extra);
        inj.injectMembers(this);
        Assert.assertNotNull(syslogRequestLog);

        Request req = createMock(Request.class);
        Response resp = createMock(Response.class);

        final StringBuffer sb = new StringBuffer("/foo");
        expect(req.getRequestURI()).andReturn(sb.toString()).anyTimes();
        expect(req.getRequestURL()).andReturn(sb).anyTimes();
        expect(req.getTimeStamp()).andReturn(10000L).anyTimes();
        expect(req.getRemoteAddr()).andReturn("1.2.3.4").anyTimes();

        final Cookie[] cookies = {new Cookie("trumpet-JSON-api-AUTHORIZATION", "omgwtfbbq")};
        expect(req.getCookies()).andReturn(cookies).anyTimes();
        expect(req.getMethod()).andReturn("GET").anyTimes();
        expect(req.getQueryString()).andReturn("bar=baz").anyTimes();
        expect(resp.getStatus()).andReturn(200).anyTimes();

        expect(resp.getHeader("X-Trumpet-Track")).andReturn("12345678-1234-1234-1234-0123456789ab").anyTimes();
        expect(resp.getHeader("Content-Length")).andReturn("42").anyTimes();

        replayAll();
        DateTimeUtils.setCurrentMillisFixed(11500);
        syslogRequestLog.log(req, resp);

        verifyAll();

        Assert.assertEquals(1, fakeSyslog.getMessages().size());
        final List<SyslogLevel> levels = fakeSyslog.getLevels();
        Assert.assertNotNull(levels);
        Assert.assertEquals(1, levels.size());
        Assert.assertEquals(SyslogLevel.INFO, levels.get(0));

        Syslog.destroyInstance("fake");
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

    public static class FakeSyslog extends AbstractNetSyslog implements SyslogIF
    {
        final List<SyslogLevel> logLevels = Lists.newArrayList();
        final List<String> logMessages = Lists.newArrayList();

        @Override
        public void flush() throws SyslogRuntimeException
        {
        }

        @Override
        public void shutdown() throws SyslogRuntimeException
        {
        }

        @Override
        protected synchronized void write(SyslogLevel level, byte[] message) throws SyslogRuntimeException
        {
            logLevels.add(level);
            logMessages.add(new String (message, getConfig().getCharSet()));
        }

        @Override
        public AbstractSyslogWriter getWriter()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void returnWriter(AbstractSyslogWriter syslogWriter)
        {
            throw new UnsupportedOperationException();
        }

        public List<String> getMessages()
        {
            return logMessages;
        }

        public List<SyslogLevel> getLevels()
        {
            return logLevels;
        }
    }
}
