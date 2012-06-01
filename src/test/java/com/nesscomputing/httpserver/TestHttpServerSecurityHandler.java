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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.galaxy.GalaxyConfigModule;
import com.nesscomputing.httpclient.HttpClient;
import com.nesscomputing.httpclient.guice.HttpClientModule;
import com.nesscomputing.httpclient.response.ContentResponseHandler;
import com.nesscomputing.httpclient.response.HttpResponse;
import com.nesscomputing.httpclient.response.HttpResponseContentConverter;
import com.nesscomputing.httpserver.jetty.ClasspathResourceHandler;
import com.nesscomputing.lifecycle.Lifecycle;
import com.nesscomputing.lifecycle.LifecycleStage;
import com.nesscomputing.lifecycle.guice.LifecycleModule;
import com.nesscomputing.testing.lessio.AllowNetworkAccess;
import com.nesscomputing.testing.lessio.AllowNetworkListen;

/**
 * manual test for the resource handler. Manual, because it uses port 8080 on localhost which could be taken.
 */
@AllowNetworkListen(ports= {0})
@AllowNetworkAccess(endpoints= {"127.0.0.1:0"})
public class TestHttpServerSecurityHandler
{
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
        baseUri = format("http://localhost:%d/foobar", port);

        final Config config = Config.getFixedConfig("galaxy.internal.port.http", Integer.toString(port));

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new ConfigModule(config),
                                                       new HttpServerModule(config),
                                                       new HttpClientModule("test"),
                                                       new LifecycleModule(),
                                                       new GalaxyConfigModule(),
                                                       new Module() {
                                                            @Override
                                                            public void configure(Binder binder) {
                                                                binder.requireExplicitBindings();
                                                                binder.disableCircularProxies();

                                                                HttpServerHandlerBinder.bindHandler(binder).toInstance(new ClasspathResourceHandler("/foobar", "/test-resources"));
                                                                HttpServerHandlerBinder.bindSecurityHandler(binder).to(VerbotenHandler.class).in(Scopes.SINGLETON);
                                                            }

                                                       });

        injector.injectMembers(this);
        lifecycle.executeTo(LifecycleStage.START_STAGE);
    }

    @After
    public void teardown()
    {
        lifecycle.executeTo(LifecycleStage.STOP_STAGE);
    }


    @Test
    public void testSimpleGet() throws Exception
    {
        final ContentResponseHandler<HttpResponse> responseHandler = ContentResponseHandler.forConverter(new HttpResponseContentConverter());

        final HttpResponse response = httpClient.get(baseUri + "/simple-content.txt", responseHandler).perform();

        Assert.assertEquals(403, response.getStatusCode());
        Assert.assertEquals("Verboten!", new String(response.getBody(), Charsets.UTF_8));
    }

    private static int findUnusedPort()
        throws IOException
    {
        int port;

        ServerSocket socket = new ServerSocket();
        try {
            socket.bind(new InetSocketAddress(0));
            port = socket.getLocalPort();
        }
        finally {
            socket.close();
        }

        return port;
    }

    @Singleton
    public static class VerbotenHandler extends HandlerWrapper
    {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            if (baseRequest.isHandled()) {
                return;
            }
            response.setStatus(403);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Verboten!");
            baseRequest.setHandled(true);
        }
    }
}
