package ness.httpserver2.selftest;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

@Path("/selftest")
public class SelfTestResource {

    private Set<SelfTest> tests = Collections.emptySet();

    @Inject
    SelfTestResource() {
    }

    @Inject(optional=true)
    public void setSelfTests(Set<SelfTest> tests) {
        this.tests = tests;
    }

	/**
	 * On success, does nothing interesting.
	 * On failure, returns a 5xx response
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public boolean doSelfTest() {
		for (SelfTest test : tests) {
		    test.doSelfTest();
		}
		return true;
	}
}
