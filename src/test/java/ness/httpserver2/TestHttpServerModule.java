package ness.httpserver2;

import static java.lang.String.format;
import io.trumpet.httpclient.HttpClient;
import io.trumpet.httpclient.guice.HttpClientModule;
import io.trumpet.httpclient.response.StringContentConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import ness.httpserver2.jetty.ClasspathResourceHandler;

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
public class TestHttpServerModule
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
        final String content = httpClient.get(baseUri + "/simple-content.txt", StringContentConverter.DEFAULT_RESPONSE_HANDLER).perform();

        Assert.assertEquals("this is simple content for a simple test\n", content);
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
