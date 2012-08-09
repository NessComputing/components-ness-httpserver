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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.galaxy.GalaxyConfigModule;
import com.nesscomputing.lifecycle.junit.LifecycleRule;
import com.nesscomputing.lifecycle.junit.LifecycleRunner;
import com.nesscomputing.lifecycle.junit.LifecycleStatement;
import com.nesscomputing.testing.lessio.AllowNetworkListen;

@AllowNetworkListen(ports= {0})
@RunWith(LifecycleRunner.class)
public class TestNullPort
{
    @LifecycleRule
    public final LifecycleStatement lifecycleRule = LifecycleStatement.serviceDiscoveryLifecycle();

    @Inject
    private HttpServer server = null;


    @Before
    public void setUp()
    {
        final Config config = Config.getFixedConfig("galaxy.internal.port.http", "0");

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       new ConfigModule(config),
                                                       new HttpServerModule(config),
                                                       new GalaxyConfigModule(),
                                                       lifecycleRule.getLifecycleModule(),
                                                       new Module() {
                                                            @Override
                                                            public void configure(final Binder binder) {
                                                                binder.requireExplicitBindings();
                                                                binder.disableCircularProxies();
                                                            }

                                                       });

        injector.injectMembers(this);
    }

    @Test
    public void testSimple() throws Exception
    {
        Assert.assertNotNull(server);
        final HttpConnector httpConnector = server.getConnectors().get("internal-http");
        Assert.assertTrue(httpConnector.getPort() > 0);
        Assert.assertTrue(httpConnector.getPort() != 8080);
        Assert.assertTrue(httpConnector.getPort() != 80);
    }
}
