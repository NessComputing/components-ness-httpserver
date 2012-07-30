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

import static com.nesscomputing.httpserver.HttpServerHandlerBinder.CATCHALL_NAME;

import java.net.URI;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * A standalone, non-galaxy http Server.
 */
public class SoloJetty8HttpServer extends AbstractJetty8HttpServer implements HttpServer
{
    private final Map<String, HttpConnector> connectors;

    @Inject
    SoloJetty8HttpServer(final SoloHttpServerConfig soloHttpServerConfig,
                           final HttpServerConfig httpServerConfig,
                           @Named(CATCHALL_NAME) final Servlet catchallServlet)
    {
        super(httpServerConfig, catchallServlet);

        final ImmutableMap.Builder<String, HttpConnector> builder = ImmutableMap.builder();

        final URI [] serviceUris = soloHttpServerConfig.getServiceUris();

        Preconditions.checkState(ArrayUtils.isNotEmpty(serviceUris), "at least one valid URI must be given!");

        int count = 0;
        for (URI serviceUri : serviceUris) {

            final String scheme = serviceUri.getScheme();
            final boolean secure = "https".equals(scheme);
            int port = serviceUri.getPort();
            if (port == 0) {
                port = secure ? 443 : 80;
            }
            builder.put(count == 0 ? "service" : String.format("service-%d", count), new HttpConnector(secure,
                                                                                                       scheme,
                                                                                                       serviceUri.getHost(),
                                                                                                       port));
            count++;
        }

        connectors = builder.build();
    }

    @Override
    public Map<String, HttpConnector> getConnectors()
    {
        return connectors;
    }
}

