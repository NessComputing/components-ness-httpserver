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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;


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

    public static LinkedBindingBuilder<HandlerWrapper> bindSecurityHandler(final Binder binder)
    {
        return binder.bind(HandlerWrapper.class).annotatedWith(Names.named("_security"));
    }
}
