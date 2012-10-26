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
