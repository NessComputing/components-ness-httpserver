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

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jetty.server.Connector;

import com.google.common.base.Preconditions;

/**
 * Describes a connector used by a server.
 */
public class HttpConnector
{
    private final boolean secure;
    private final int port;
    private final String address;
    private final String scheme;

    private final AtomicReference<Connector> connectorHolder = new AtomicReference<Connector>(null);


    public HttpConnector(final boolean secure,
                         @Nonnull final String scheme,
                         @Nonnull final String address,
                         final int port)
    {
        Preconditions.checkNotNull(scheme, "the scheme can not be null");
        Preconditions.checkNotNull(address, "the address can not be null");

        this.secure = secure;
        this.scheme = scheme;
        this.address = address;
        this.port = port;
    }

    /**
     * Returns true if this is a secure connector.
     */
    public boolean isSecure()
    {
        return secure;
    }

    /**
     * Will be called when jetty should choose a port to update the port information.
     */
    void setJettyConnector(final Connector connector)
    {
        this.connectorHolder.set(connector);
    }

    /**
     * Returns the system port for this connector.
     */
    public int getPort()
    {
        if (port != 0) {
            return port;
        }
        else {
            final Connector connector = connectorHolder.get();
            if (connector != null) {
                Preconditions.checkState(connector.getLocalPort() > 0, "no port was set and the connector is not yet started!");
                return connector.getLocalPort();
            }
            else {
                return 0;
            }
        }
    }

    /**
     * Returns the address for this connector as a string.
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * Returns the scheme for this connector.
     */
    public String getScheme()
    {
        return scheme;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof HttpConnector)) {
            return false;
        }
        HttpConnector castOther = (HttpConnector) other;
        return new EqualsBuilder().append(secure, castOther.secure).append(port, castOther.port).append(address, castOther.address).append(scheme, castOther.scheme).isEquals();
    }

    private transient int hashCode;


    @Override
    public int hashCode()
    {
        if (hashCode == 0) {
            hashCode = new HashCodeBuilder().append(secure).append(port).append(address).append(scheme).toHashCode();
        }
        return hashCode;
    }

    private transient String toString;

    @Override
    public String toString()
    {
        if (toString == null) {
            toString = scheme + "://" + address + ":" + port + " (" + (secure ? "secure" : "not secure") + ")";
        }
        return toString;
    }
}
