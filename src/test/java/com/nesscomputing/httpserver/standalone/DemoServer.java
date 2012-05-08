package com.nesscomputing.httpserver.standalone;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.nesscomputing.config.Config;
import com.nesscomputing.httpserver.standalone.StandaloneServer;

class DemoServer extends StandaloneServer
{
    private static DemoServer server = null;
    private final Config config;

    public DemoServer(Config config) {
        this.config = config;
    }

    public static void main(final String [] args)
    {
        server = new DemoServer(null);
        server.startServer();
    }

    @Override
    public Config getConfig() {
        if (config != null) {
            return config;
        }
        return super.getConfig();
    }

    public static StandaloneServer getServer()
    {
        return server;
    }

    @Override
    public Module getMainModule(final Config config)
    {
        return new Module() {
            @Override
            public void configure(final Binder binder)
            {
            }
        };
    }
}
