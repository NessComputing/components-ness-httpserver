package com.nesscomputing.httpserver.standalone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.standalone.StandaloneServer;
import com.nesscomputing.testing.lessio.AllowNetworkAccess;
import com.nesscomputing.testing.lessio.AllowNetworkListen;

@AllowNetworkListen(ports= {0})
@AllowNetworkAccess(endpoints= {"127.0.0.1:0"})
public class TestServerLifecycle
{
    private StandaloneServer server = null;

    @Before
    public void setUp()
    {
        Assert.assertNull(server);
        server = new StandaloneServer() {
            @Override
            public Config getConfig() {
                return Config.getEmptyConfig();
            }

            @Override
            protected Module getMainModule(final Config config) {
                return new Module() {
                    @Override
                    public void configure(final Binder binder) {
                    }
                };
            }
        };
    }

    @After
    public void tearDown()
    {
        Assert.assertNotNull(server);

        if (!server.isStarted()) {
            server.startServer();
        }

        if (!server.isStopped()) {
            server.stopServer();
        }

        server = null;
    }


    @Test
    public void testLifecycle() throws Exception
    {
        Assert.assertFalse(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.startServer();

        Assert.assertTrue(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.stopServer();

        Assert.assertTrue(server.isStarted());
        Assert.assertTrue(server.isStopped());
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleStart() throws Exception
    {
        Assert.assertFalse(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.startServer();

        Assert.assertTrue(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.startServer();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleStop() throws Exception
    {
        Assert.assertFalse(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.startServer();

        Assert.assertTrue(server.isStarted());
        Assert.assertFalse(server.isStopped());

        server.stopServer();

        Assert.assertTrue(server.isStarted());
        Assert.assertTrue(server.isStopped());

        server.stopServer();
    }

}
