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
package com.nesscomputing.httpserver;

import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.google.inject.servlet.ServletModule;

import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.galaxy.GalaxyConfigModule;
import com.nesscomputing.httpclient.HttpClient;
import com.nesscomputing.httpclient.HttpClientResponse;
import com.nesscomputing.httpclient.HttpClientResponseHandler;
import com.nesscomputing.httpclient.guice.HttpClientModule;
import com.nesscomputing.lifecycle.Lifecycle;
import com.nesscomputing.lifecycle.LifecycleStage;
import com.nesscomputing.lifecycle.guice.LifecycleModule;

import net.jpountz.lz4.LZ4BlockInputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitei.testing.lessio.AllowNetworkAccess;
import org.kitei.testing.lessio.AllowNetworkListen;

/**
 * manual test for the resource handler. Manual, because it uses port 8080 on localhost which could be taken.
 */
@AllowNetworkListen(ports= {0})
@AllowNetworkAccess(endpoints= {"127.0.0.1:0"})
public class TestHttpCompression
{
    private static final String BIG_CONTENT_RESOURCE = "/test-resources/big-content.txt";

    @Inject
    @Named("test")
    private final HttpClient httpClient = null;

    @Inject
    private final Lifecycle lifecycle = null;

    private String baseUri = null;

    private int port = 0;

    @Before
    public void setUp() throws Exception
    {
        port = findUnusedPort();
        Assert.assertFalse(port == 0);
        baseUri = format("http://localhost:%d/", port);

        final Config config = Config.getFixedConfig("galaxy.internal.port.http", Integer.toString(port));

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new ConfigModule(config),
                                                       new HttpServerModule(config),
                                                       new HttpClientModule("test"),
                                                       new LifecycleModule(),
                                                       new GalaxyConfigModule(),
                                                       new ServletModule() {
                                                            @Override
                                                            public void configureServlets() {
                                                                binder().requireExplicitBindings();
                                                                binder().disableCircularProxies();

                                                                bind (ContentServlet.class);
                                                                serve("/content").with(ContentServlet.class);
                                                            }

                                                       });

        injector.injectMembers(this);
        lifecycle.executeTo(LifecycleStage.START_STAGE);
    }

    @Singleton
    static class ContentServlet extends HttpServlet
    {
        private static final long serialVersionUID = 1L;

        private ServletConfig config;

        @Override
        public void init(ServletConfig configIn) throws ServletException
        {
            this.config = configIn;
        }

        @Override
        public ServletConfig getServletConfig()
        {
            return config;
        }

        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
        {
            res.setStatus(HttpStatus.OK_200);
            res.setContentType(MediaType.PLAIN_TEXT_UTF_8.toString());
            IOUtils.copy(TestHttpCompression.class.getResourceAsStream(BIG_CONTENT_RESOURCE), res.getOutputStream());
        }

        @Override
        public String getServletInfo()
        {
            return "Content Servlet";
        }

        @Override
        public void destroy()
        {
        }
    }

    @After
    public void teardown()
    {
        lifecycle.executeTo(LifecycleStage.STOP_STAGE);
    }

    private static int findUnusedPort()
        throws IOException
    {
        int port;

        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress(0));
            port = socket.getLocalPort();
        }

        return port;
    }


    private static final String BIG_CONTENT;

    static {
        try {
            BIG_CONTENT = IOUtils.toString(TestHttpCompression.class.getResourceAsStream(BIG_CONTENT_RESOURCE));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static class GzipHttpClientResponseHandler implements HttpClientResponseHandler<String>
    {
        @Override
        public String handle(HttpClientResponse response) throws IOException
        {
            Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
            Assert.assertEquals("gzip", response.getHeader(HttpHeaders.CONTENT_ENCODING));
            return IOUtils.toString(new GZIPInputStream(response.getResponseBodyAsStream()), Charsets.UTF_8);
        }
    }

    static class LZ4HttpClientResponseHandler implements HttpClientResponseHandler<String>
    {
        @Override
        public String handle(HttpClientResponse response) throws IOException
        {
            Assert.assertEquals(HttpStatus.OK_200, response.getStatusCode());
            Assert.assertEquals("lz4", response.getHeader(HttpHeaders.CONTENT_ENCODING));
            return IOUtils.toString(new LZ4BlockInputStream(response.getResponseBodyAsStream()), Charsets.UTF_8);
        }
    }

    @Test
    public void testGzip() throws Exception
    {
        final String content = httpClient.get(baseUri + "/content", new GzipHttpClientResponseHandler())
                .addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
                .perform();

        Assert.assertEquals(BIG_CONTENT, content);
    }

    @Test
    public void testLZ4() throws Exception
    {
        final String content = httpClient.get(baseUri + "/content", new LZ4HttpClientResponseHandler())
                .addHeader(HttpHeaders.ACCEPT_ENCODING, "lz4")
                .perform();

        Assert.assertEquals(BIG_CONTENT, content);
    }
}
