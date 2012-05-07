package ness.httpserver2;

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
     * @return the internal address this HttpServer listens on.  null if no such interface, empty string for wildcard bind
     */
    String getInternalAddress();

    /**
     * @return the external address this HttpServer listens on.  null if no such interface, empty string for wildcard bind
     */
    String getExternalAddress();

    /**
     * Return the actual internal http port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on
     */
    int getInternalHttpPort();

    /**
     * Return the actual internal https port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on
     */
    int getInternalHttpsPort();

    /**
     * Return the actual external http port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on
     */
    int getExternalHttpPort();

    /**
     * Return the actual external https port this server listens to.  Mostly useful when you configure the http server on a port of "0" to get an unused port.
     * @return the actual port this server listens on
     */
    int getExternalHttpsPort();
}
