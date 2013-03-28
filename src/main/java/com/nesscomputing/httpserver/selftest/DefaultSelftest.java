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

import javax.ws.rs.core.Response;

public interface DefaultSelftest {
    /**
     * Check that the service is in a good state.
     * When a DefaultSelftest is bound, it will be evaluated after all bound Selftests have
     * returned without throwing exceptions.
     *
     * @return Response code 2xx for OK, 3xx for WARN {4,5}xx for ERROR.
     * @throw Also supports throwing an exception to signal error.
     */
    Response doSelftest() throws Exception;
}
