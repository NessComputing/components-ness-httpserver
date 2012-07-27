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

import java.util.Map;

/**
 * Represents an HTTP server. It can be started and stopped.
 */
public interface HttpServer
{
    /**
     * Bring up the HTTP Server.
     */
    void start();

    /**
     * Shut down the HTTP Server.
     */
    void stop();

    /**
     * @return the internal address this HttpServer listens on.  null if no such interface, empty string for wildcard bind.
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    String getInternalAddress();

    /**
     * @return the external address this HttpServer listens on.  null if no such interface, empty string for wildcard bind.
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    String getExternalAddress();

    /**
     * Return the actual internal http port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on.
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    int getInternalHttpPort();

    /**
     * Return the actual internal https port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on.
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    int getInternalHttpsPort();

    /**
     * Return the actual external http port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on.
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    int getExternalHttpPort();

    /**
     * Return the actual external https port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on
     *
     * @deprecated Use {@link HttpServer#getConnectors()}.
     */
    @Deprecated
    int getExternalHttpsPort();

    /**
     * Return a map of connectors configured for this server. The key in the map can be used to distinguish the connectors.
     */
    Map<String, HttpConnector> getConnectors();
}
