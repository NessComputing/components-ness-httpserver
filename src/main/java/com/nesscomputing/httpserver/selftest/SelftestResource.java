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
import com.google.inject.Inject;

@Path("/selftest")
public class SelftestResource {

    private Set<Selftest> tests = Collections.emptySet();

    @Inject
    SelftestResource() {
    }

    @Inject(optional=true)
    public void setSelftests(Set<Selftest> tests) {
        this.tests = tests;
    }

	/**
	 * On success, does nothing interesting.
	 * On failure, returns a 5xx response
	 */
	@GET
	public void doSelftest() throws Exception {
		for (Selftest test : tests) {
		    test.doSelftest();
		}
	}
}
