package com.nesscomputing.httpserver.standalone;

import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.standalone.AnnouncingStandaloneServer;
import com.nesscomputing.httpserver.standalone.StandaloneServer;
import com.nesscomputing.testing.lessio.AllowNetworkAccess;
import com.nesscomputing.testing.lessio.AllowNetworkListen;


@AllowNetworkListen(ports= {0})
@AllowNetworkAccess(endpoints= {"127.0.0.1:0"})
public class TestStandaloneServer
{
    @Test
    public void testBasic()
    {
        final StandaloneServer server = DemoServer.getServer();
        Assert.assertNull(server);
    }

    @Test
    public void testSpinup() throws Exception
    {
        final StandaloneServer server = new DemoServer(Config.getEmptyConfig());
        server.startServer();

        try {
            Assert.assertNotNull(server);
            Assert.assertTrue(server.isStarted());
            Assert.assertFalse(server.isStopped());
        }
        finally {
            server.stopServer();
            Assert.assertTrue(server.isStarted());
            Assert.assertTrue(server.isStopped());
        }
    }

    @Test
    public void testAnnouncingBasic()
    {
        final AnnouncingStandaloneServer server = AnnouncingDemoServer.getServer();
        Assert.assertNull(server);
    }

    @Test
    public void testAnnouncingSpinup() throws Exception
    {
        final AnnouncingStandaloneServer server = new AnnouncingDemoServer(Config.getEmptyConfig());
        server.startServer();
        try {
            Assert.assertNotNull(server);
            Assert.assertTrue(server.isStarted());
            Assert.assertFalse(server.isStopped());
        }
        finally {
            server.stopServer();
            Assert.assertTrue(server.isStarted());
            Assert.assertTrue(server.isStopped());
        }
    }
}
