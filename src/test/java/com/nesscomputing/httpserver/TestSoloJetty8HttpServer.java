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

import com.google.common.io.Resources;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Named;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.httpclient.HttpClient;
import com.nesscomputing.httpclient.guice.HttpClientModule;
import com.nesscomputing.httpclient.response.StringContentConverter;
import com.nesscomputing.httpserver.jetty.ClasspathResourceHandler;
import com.nesscomputing.lifecycle.Lifecycle;
import com.nesscomputing.lifecycle.LifecycleStage;
import com.nesscomputing.lifecycle.guice.LifecycleModule;
import com.nesscomputing.testing.lessio.AllowNetworkAccess;
import com.nesscomputing.testing.lessio.AllowNetworkListen;

@AllowNetworkListen(ports= {0})
@AllowNetworkAccess(endpoints= {"127.0.0.1:0"})
public class TestSoloJetty8HttpServer
{
    @Inject
    @Named("test")
    private final HttpClient httpClient = null;

    @Inject
    private final Lifecycle lifecycle = null;

    private String [] createServer(int ... port) throws Exception
    {
        String [] uris = new String [port.length];

        for (int i = 0; i < port.length; i++) {
            Assert.assertFalse(port[i] == 0);
            uris[i] = format("http://localhost:%d/foobar", port[i]);
        }

        return createServer(uris);
    }

    private String [] createServer(String ... uris)
    {
        final Config config = Config.getFixedConfig("ness.httpserver.service-uri", StringUtils.join(uris, ","),
                                                    "ness.httpserver.class", "com.nesscomputing.httpserver.SoloJetty8HttpServer",
                                                    "ness.httpserver.ssl-keystore", Resources.getResource(this.getClass(), "/ssl-server-keystore.jks").toString(),
                                                    "ness.httpclient.ssl.truststore", Resources.getResource(this.getClass(), "/test-httpclient-keystore.jks").toString(),
                                                    "ness.httpclient.ssl.truststore.password", "verysecret"
                                                    );

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new ConfigModule(config),
                                                       new HttpServerModule(config),
                                                       new HttpClientModule("test"),
                                                       new LifecycleModule(),
                                                       new Module() {
                                                            @Override
                                                            public void configure(Binder binder) {
                                                                binder.requireExplicitBindings();
                                                                binder.disableCircularProxies();

                                                                HttpServerHandlerBinder.bindHandler(binder).toInstance(new ClasspathResourceHandler("/foobar", "/test-resources"));
                                                            }

                                                       });

        injector.injectMembers(this);
        Assert.assertNotNull(lifecycle);

        lifecycle.executeTo(LifecycleStage.START_STAGE);
        return uris;
    }

    @After
    public void tearDown() throws Exception
    {
        Assert.assertNotNull(lifecycle);

        lifecycle.executeTo(LifecycleStage.STOP_STAGE);
    }


    @Test
    public void testSimple() throws Exception
    {
        final String baseUri = createServer(findUnusedPort())[0];

        final String content = httpClient.get(baseUri + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER).perform();

        Assert.assertEquals("this is simple content for a simple test\n", content);
    }

    @Test
    public void testTwoUrisOneServer() throws Exception
    {
        final String [] baseUris = createServer(findUnusedPort(), findUnusedPort());

        for (int i = 0; i < baseUris.length; i++) {
            final String content = httpClient.get(baseUris[i] + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER).perform();
            Assert.assertEquals("this is simple content for a simple test\n", content);
        }
    }

    @Test
    public void testDoubleWhammy() throws Exception
    {
        int port = findUnusedPort();
        final String [] baseUris = createServer(port, port);

        for (int i = 0; i < baseUris.length; i++) {
            final String content = httpClient.get(baseUris[i] + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER).perform();
            Assert.assertEquals("this is simple content for a simple test\n", content);
        }
    }

    @Test
    public void testYesWeCanHttps() throws Exception
    {
        final String [] baseUris = createServer(format("http://localhost:%d/foobar", findUnusedPort()),
                                                format("https://localhost:%d/foobar", findUnusedPort()));

        for (int i = 0; i < baseUris.length; i++) {
            final String content = httpClient.get(baseUris[i] + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER).perform();
            Assert.assertEquals("this is simple content for a simple test\n", content);
        }
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
}
