package ness.httpserver2;

import org.eclipse.jetty.server.Handler;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;


/**
 * Bind additional {@link Handler} elements into the http server.
 */
public final class HttpServerHandlerBinder
{
    private HttpServerHandlerBinder()
    {
    }

    public static LinkedBindingBuilder<Handler> bindHandler(final Binder binder)
    {
        final Multibinder<Handler> handlers = Multibinder.newSetBinder(binder, Handler.class);
        return handlers.addBinding();
    }
}
