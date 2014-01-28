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

import java.util.Map;

import javax.servlet.Servlet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.nesscomputing.galaxy.GalaxyConfig;
import com.nesscomputing.galaxy.GalaxyIp;

public class GalaxyJetty8HttpServer extends AbstractJetty8HttpServer implements HttpServer
{
    private final Map<String, HttpConnector> httpConnectors;

    @Inject
    GalaxyJetty8HttpServer(final GalaxyConfig galaxyConfig,
                           final GalaxyHttpServerConfig galaxyHttpServerConfig,
                           final HttpServerConfig httpServerConfig,
                           @Named(CATCHALL_NAME) final Servlet catchallServlet)
    {
        super(httpServerConfig, catchallServlet);

        this.httpConnectors = buildConnectors(galaxyHttpServerConfig, galaxyConfig);
    }

    @Override
    public Map<String, HttpConnector> getConnectors()
    {
        return httpConnectors;
    }

    private static Map<String, HttpConnector> buildConnectors(final GalaxyHttpServerConfig galaxyHttpServerConfig, final GalaxyConfig galaxyConfig)
    {
        final GalaxyIp internalIp = galaxyConfig.getInternalIp();
        final GalaxyIp externalIp = galaxyConfig.getExternalIp();

        final Builder<String, HttpConnector> builder = ImmutableMap.<String, HttpConnector>builder();

        if (galaxyHttpServerConfig.isInternalHttpEnabled()) {
            builder.put("internal-http", new HttpConnector(false, "http", internalIp.getIp(), internalIp.getHttpPort()));
        }

        if (galaxyHttpServerConfig.isInternalHttpsEnabled()) {
            builder.put("internal-https", new HttpConnector(true, "https", internalIp.getIp(), internalIp.getHttpsPort()));
        }

        if (galaxyHttpServerConfig.isExternalHttpEnabled()) {
            builder.put("external-http", new HttpConnector(false, "http", externalIp.getIp(), internalIp.getHttpPort()));
        }

        if (galaxyHttpServerConfig.isExternalHttpsEnabled()) {
            builder.put("external-https", new HttpConnector(true, "https", externalIp.getIp(), internalIp.getHttpsPort()));
        }

        return builder.build();
    }
}

