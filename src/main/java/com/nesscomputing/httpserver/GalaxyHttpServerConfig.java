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

import org.skife.config.Config;
import org.skife.config.Default;

public abstract class GalaxyHttpServerConfig
{
    @Config("ness.httpserver.internal.http")
    @Default("true")
    public boolean isInternalHttpEnabled()
    {
        return true;
    }

    @Config("ness.httpserver.internal.https")
    @Default("false")
    public boolean isInternalHttpsEnabled()
    {
        return false;
    }

    @Config("ness.httpserver.external.http")
    @Default("false")
    public boolean isExternalHttpEnabled()
    {
        return false;
    }

    @Config("ness.httpserver.external.https")
    @Default("false")
    public boolean isExternalHttpsEnabled()
    {
        return false;
    }
}
