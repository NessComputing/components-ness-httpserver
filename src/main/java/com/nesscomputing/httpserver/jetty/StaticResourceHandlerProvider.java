package com.nesscomputing.httpserver.jetty;

import java.io.File;

import javax.annotation.Nonnull;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nesscomputing.httpserver.HttpServerConfig;

/**
 * Serves static resources from a path in the filesystem. The location should be in the galaxy tarball and set by configuration.
 */
public class StaticResourceHandlerProvider implements Provider<Handler>
{
    private final String contextPath;
    private HttpServerConfig httpServerConfig = null;

    public StaticResourceHandlerProvider(@Nonnull final String contextPath)
    {
        Preconditions.checkArgument(contextPath != null, "context path can not be null (use \"\" for the root!");
        this.contextPath = contextPath;
    }

    @Inject
    void injectHttpServerConfig(final HttpServerConfig httpServerConfig)
    {
        this.httpServerConfig = httpServerConfig;
    }

    @Override
    public Handler get()
    {
        Preconditions.checkState(httpServerConfig != null, "http server config was never injected!");

        final ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath(contextPath);

        final File basePath = httpServerConfig.getServletContextBasePath();
        Preconditions.checkState(basePath != null, "no base path was set in the config!");
        Preconditions.checkState(basePath.exists(), "base path %s does not exist!", basePath.getAbsolutePath());
        Preconditions.checkState(basePath.canRead(), "base path %s is unreadable!", basePath.getAbsolutePath());
        Preconditions.checkState(basePath.isDirectory(), "base path %s is not a directory!", basePath.getAbsolutePath());

        contextHandler.setResourceBase(basePath.getAbsolutePath());
        contextHandler.setHandler(new ResourceHandler());

        return contextHandler;
    }
}
