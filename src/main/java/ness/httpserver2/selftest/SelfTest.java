package ness.httpserver2.selftest;

public interface SelfTest {
    /**
     * Check that service is available.
     * @throw RuntimeException the service is not available
     */
    void doSelfTest();
}
