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
package com.nesscomputing.httpserver.selftest;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

@Path("/selftest")
@Produces(MediaType.APPLICATION_JSON)
public class SelftestResource {

    private Set<Selftest> tests = Collections.emptySet();
    private DefaultSelftest defaultTest;

    @Inject
    SelftestResource() {
    }

    @Inject(optional=true)
    public void setSelftests(Set<Selftest> tests) {
        this.tests = tests;
    }

    @Inject(optional=true)
    public void setDefaultSelftest(DefaultSelftest defaultTest) {
        this.defaultTest = defaultTest;
    }

    /**
     * On success, returns a 2xx response.
     * To report a warning-level issue, returns a 3xx response.
     * To report an error-level issue, returns a 5xx response
     * or throws an Exception.
     */
    @GET
    public Response doSelftest() throws Exception {
        for (Selftest test : tests) {
            test.doSelftest();
        }

        if (defaultTest != null) {
            return defaultTest.doSelftest();
        }
        else {
            return Response.ok().build();
        }
    }
}
