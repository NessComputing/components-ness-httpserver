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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.Cookie;


import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.log.LogFieldsModule;
import com.nesscomputing.httpserver.log.file.FileRequestLog;
import com.nesscomputing.httpserver.log.file.FileRequestLogModule;

public class FileRequestLogTest extends EasyMockSupport
{

    @Inject
    private FileRequestLog fileRequestLog;

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testRequestLogging()
    {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.file.enabled", "true",
                                                    "ness.httpserver.request-log.file.fields", "remoteAddr,cookie:trumpet-json-api-authorization,cookie:not-here,method,requestUri,query,responseCode,responseHeader:Content-Length,elapsedTime");
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(),  new FileRequestLogModule(config));
        inj.injectMembers(this);
        Assert.assertNotNull(fileRequestLog);

        StringWriter buffer = new StringWriter();
        fileRequestLog.setWriter(new PrintWriter(buffer));

        Request req = createMock(Request.class);
        Response resp = createMock(Response.class);

        expect(req.getRemoteAddr()).andReturn("1.2.3.4").anyTimes();
        Cookie[] cookies = {new Cookie("trumpet-JSON-api-AUTHORIZATION", "omgwtfbbq")};
        expect(req.getCookies()).andReturn(cookies).anyTimes();
        expect(req.getMethod()).andReturn("GET").anyTimes();
        expect(req.getRequestURI()).andReturn("foo").anyTimes();
        expect(req.getRequestURL()).andReturn(new StringBuffer("foo")).anyTimes();
        expect(req.getQueryString()).andReturn("?bar").anyTimes();
        expect(req.getTimeStamp()).andReturn(10000L).anyTimes();
        expect(resp.getStatus()).andReturn(201).anyTimes();
        expect(resp.getHeader("Content-Length")).andReturn("42").anyTimes();

        replayAll();

        DateTimeUtils.setCurrentMillisFixed(11500);

        fileRequestLog.log(req, resp);

        assertEquals("1.2.3.4\tomgwtfbbq\t\tGET\tfoo\t?bar\t201\t42\t1500\n", buffer.getBuffer().toString());
        verifyAll();
    }

    @Test
    public void testNullCookies() {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.file.enabled", "true",
                                                    "ness.httpserver.request-log.file.fields", "remoteAddr,cookie:trumpet-json-api-authorization,method,requestUri,query,responseCode,responseHeader:Content-Length");
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(),  new FileRequestLogModule(config));
        inj.injectMembers(this);
        Assert.assertNotNull(fileRequestLog);

                StringWriter buffer = new StringWriter();
        fileRequestLog.setWriter(new PrintWriter(buffer));

        Request req = createMock(Request.class);
        Response resp = createMock(Response.class);

        expect(req.getRemoteAddr()).andReturn("1.2.3.4").anyTimes();
        expect(req.getCookies()).andReturn(null).anyTimes();
        expect(req.getMethod()).andReturn("GET").anyTimes();
        expect(req.getRequestURL()).andReturn(new StringBuffer("foo")).anyTimes();
        expect(req.getRequestURI()).andReturn("foo").anyTimes();
        expect(req.getQueryString()).andReturn("?bar").anyTimes();
        expect(resp.getStatus()).andReturn(201).anyTimes();
        expect(resp.getHeader("Content-Length")).andReturn("42").anyTimes();

        replayAll();
        fileRequestLog.log(req, resp);

        assertEquals("1.2.3.4\t\tGET\tfoo\t?bar\t201\t42\n", buffer.getBuffer().toString());
        verifyAll();
    }

    @Test
    public void testInvalidLogPatternBadSpecifier() {
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.file.enabled", "true",
                                                    "ness.httpserver.request-log.file.fields", "remoteAddr,foo");

        try {
            final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new FileRequestLogModule(config));
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
            final Config config = Config.getFixedConfig("ness.httpserver.request-log.file.enabled", "true",
                                                        "ness.httpserver.request-log.file.fields", "remoteAddr,cookie:no-here:bar");
            final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new FileRequestLogModule(config));
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
        final Config config = Config.getFixedConfig("ness.httpserver.request-log.file.enabled", "true");
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new FileRequestLogModule(config));
        inj.injectMembers(this);
        Assert.assertNotNull(fileRequestLog);
    }

    @Test
    public void testDisabledByDefault()
    {
        final Config config = Config.getEmptyConfig();
        final Injector inj = Guice.createInjector(Stage.PRODUCTION, disableStuff(), new LogFieldsModule(), new FileRequestLogModule(config));

        Assert.assertNull(inj.getExistingBinding(Key.get(FileRequestLog.class)));
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
