package ness.httpserver2;

import static java.lang.String.format;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ness.httpserver2.jetty.ClasspathResourceHandler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.galaxy.GalaxyConfigModule;
import com.nesscomputing.httpclient.HttpClient;
import com.nesscomputing.httpclient.guice.HttpClientModule;
import com.nesscomputing.httpclient.response.StringContentConverter;
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
public class TestHttpServerForwardedHeaders
{
    @Inject
    @Named("test")
    private final HttpClient httpClient = null;

    @Inject
    private final Lifecycle lifecycle = null;

    private String baseUri = null;

    private int port = 0;

    private RecordingHandlerWrapper handler = null;

    @Before
    public void setUp() throws Exception
    {
        port = findUnusedPort();
        Assert.assertFalse(port == 0);
        baseUri = format("http://localhost:%d/foobar", port);

        final Config config = Config.getFixedConfig("galaxy.internal.port.http", Integer.toString(port),
                                                    "ness.httpserver.internal.http-forwarded", "true");

        handler = new RecordingHandlerWrapper(new ClasspathResourceHandler("/foobar", "/test-resources"));

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

                                                                HttpServerHandlerBinder.bindHandler(binder).toInstance(handler);
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
        final String content = httpClient.get(baseUri + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER)
                                         .addHeader("X-Forwarded-For", "1.2.3.4")
                                         .addHeader("X-Forwarded-Server", "www.likeness.com")
                                         .addHeader("X-Forwarded-Proto", "https")
                                         .perform();

        Assert.assertEquals("this is simple content for a simple test\n", content);

        Assert.assertEquals("www.likeness.com", handler.getLocalAddr());
        Assert.assertEquals(443, handler.getLocalPort().intValue());
        Assert.assertEquals("1.2.3.4", handler.getRemoteAddr());

        Assert.assertTrue(handler.getSecure().booleanValue());
        Assert.assertEquals("https", handler.getScheme());
    }

    public static class RecordingHandlerWrapper extends AbstractHandler
    {
        private final AbstractHandler wrappedHandler;

        private String localAddr = null;
        private Integer localPort = null;
        private String remoteAddr = null;
        private Integer remotePort = null;
        private Boolean secure = null;
        private String scheme = null;

        RecordingHandlerWrapper(final AbstractHandler wrappedHandler)
        {
            this.wrappedHandler = wrappedHandler;
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            localAddr = request.getServerName();
            localPort = request.getServerPort();
            secure = request.isSecure();
            remoteAddr = request.getRemoteHost();
            remotePort = request.getRemotePort();
            scheme = request.getScheme();

            wrappedHandler.handle(target, baseRequest, request, response);
        }

        public String getLocalAddr()
        {
            return localAddr;
        }

        public Integer getLocalPort()
        {
            return localPort;
        }

        public String getRemoteAddr()
        {
            return remoteAddr;
        }

        public Integer getRemotePort()
        {
            return remotePort;
        }

        public Boolean getSecure()
        {
            return secure;
        }

        public String getScheme()
        {
            return scheme;
        }
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
}
