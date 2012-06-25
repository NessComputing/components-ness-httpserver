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

import java.lang.annotation.Annotation;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;


/**
 * Bind additional {@link Handler} elements into the http server.
 */
public final class HttpServerHandlerBinder
{
    public static final String HANDLER_NAME = "_handlers";
    public static final Annotation HANDLER_NAMED = Names.named(HANDLER_NAME);

    public static final String LOGGING_NAME = "_logging";
    public static final Annotation LOGGING_NAMED = Names.named(LOGGING_NAME);

    public static final String SECURITY_NAME = "_security";
    public static final Annotation SECURITY_NAMED = Names.named(SECURITY_NAME);

    public static final String CATCHALL_NAME = "_catchall";
    public static final Annotation CATCHALL_NAMED = Names.named(CATCHALL_NAME);

    private HttpServerHandlerBinder()
    {
    }

    /**
     * Bind a new handler into the jetty service. These handlers are bound before the servlet handler and can handle parts
     * of the URI space before a servlet sees it.
     *
     * Do not use this method to bind logging handlers as they would be called before any functionality has been executed and
     * logging information would be incomplete. Use {@link HttpServerHandlerBinder#bindLoggingHandler(Binder) instead.
     */
    public static LinkedBindingBuilder<Handler> bindHandler(final Binder binder)
    {
        final Multibinder<Handler> handlers = Multibinder.newSetBinder(binder, Handler.class, HANDLER_NAMED);
        return handlers.addBinding();
    }

    /**
     * Bind a new handler into the jetty service. These handlers are bound after the servlet handler and will not see any requests before they
     * have been handled. This is intended to bind logging, statistics etc. which want to operate on the results of a request to the server.
     *
     * Any handler intended to manage content or a part of the URI space should use {@link HttpServerHandlerBinder#bindHandler(Binder)}.
     */
    public static LinkedBindingBuilder<Handler> bindLoggingHandler(final Binder binder)
    {
        final Multibinder<Handler> handlers = Multibinder.newSetBinder(binder, Handler.class, LOGGING_NAMED);
        return handlers.addBinding();
    }

    /**
     * Bind a delegating security handler. Any request will be routed through this handler and can be allowed or denied by this handler. If no handler
     * is bound, requests are forwarded to the internal handler collection.
     */
    public static LinkedBindingBuilder<HandlerWrapper> bindSecurityHandler(final Binder binder)
    {
        return binder.bind(HandlerWrapper.class).annotatedWith(SECURITY_NAMED);
    }

    /**
     * Configure the "get all requests" servlet that backs the {@link GuiceFilter}. This servlet should log all requests as legal request should never
     * hit it.
     */
    public static LinkedBindingBuilder<Servlet> bindCatchallServlet(final Binder binder)
    {
        return binder.bind (Servlet.class).annotatedWith(CATCHALL_NAMED);
    }
}
