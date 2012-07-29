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
package com.nesscomputing.httpserver.standalone;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.nesscomputing.config.Config;

@SuppressWarnings("deprecation")
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
