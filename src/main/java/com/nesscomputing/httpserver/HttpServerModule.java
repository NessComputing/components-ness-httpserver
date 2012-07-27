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

import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigProvider;
import com.nesscomputing.httpserver.log.LogFieldsModule;
import com.nesscomputing.httpserver.log.file.FileRequestLogModule;
import com.nesscomputing.httpserver.log.log4j.Log4jRequestLogModule;
import com.nesscomputing.httpserver.log.syslog.SyslogRequestLogModule;

public class HttpServerModule extends ServletModule
{
    /** Should match the value of the constant in tc-servlet GuiceListener for war-based apps. */
    public static final String CONTEXT_PATH_NAME = "_contextPath";

    private final Config config;

    public HttpServerModule(final Config config)
    {
        this.config = config;
    }

    @Override
    public void configureServlets()
    {
        final HttpServerConfig httpServerConfig = config.getBean(HttpServerConfig.class);
        bind(HttpServerConfig.class).toInstance(httpServerConfig);
        bind(GalaxyHttpServerConfig.class).toProvider(ConfigProvider.of(GalaxyHttpServerConfig.class)).in(Scopes.SINGLETON);

        // Bind the legacy CONTEXT_PATH_NAME constants to the first path in the service. Do we actually still need that?
        bindConstant().annotatedWith(Names.named(CONTEXT_PATH_NAME)).to("");

        install (new LogFieldsModule());
        install (new FileRequestLogModule(config));
        install (new Log4jRequestLogModule(config));
        install (new SyslogRequestLogModule(config));

        final Class<? extends HttpServer> serverClass = httpServerConfig.getServerClass();
        bind (serverClass).asEagerSingleton();
        bind (HttpServer.class).to(serverClass).asEagerSingleton();

        HttpServerHandlerBinder.bindCatchallServlet(binder()).to(InvalidRequestServlet.class).in(Scopes.SINGLETON);

        bind (GuiceFilter.class).in(Scopes.SINGLETON);
    }
}
