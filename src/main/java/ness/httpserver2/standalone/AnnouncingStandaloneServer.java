package ness.httpserver2.standalone;

import com.nesscomputing.lifecycle.LifecycleStage;
import com.nesscomputing.lifecycle.ServiceDiscoveryLifecycle;
import com.nesscomputing.lifecycle.guice.LifecycleModule;

import com.google.inject.Module;

/**
 * Standalone server that uses the ServiceDiscovery Lifecycle. No additional
 * announcements etc. are done.
 */
public abstract class AnnouncingStandaloneServer extends StandaloneServer
{
    @Override
    protected LifecycleStage getStartStage()
    {
        return LifecycleStage.ANNOUNCE_STAGE;
    }

    @Override
    protected Module getLifecycleModule()
    {
        return new LifecycleModule(ServiceDiscoveryLifecycle.class);
    }
}
