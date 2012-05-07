package ness.httpserver2.standalone;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.nesscomputing.config.Config;

class AnnouncingDemoServer extends AnnouncingStandaloneServer
{
    private static AnnouncingDemoServer server = null;
    private final Config config;

    public AnnouncingDemoServer(Config config) {
        this.config = config;
    }

    public static void main(final String [] args)
    {
        server = new AnnouncingDemoServer(null);
        server.startServer();
    }

    @Override
    public Config getConfig() {
        if (config != null) {
            return config;
        }
        return super.getConfig();
    }

    public static AnnouncingStandaloneServer getServer()
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
