package ness.httpserver2;

import javax.servlet.Servlet;

import ness.httpserver2.log.LogFieldsModule;
import ness.httpserver2.log.file.FileRequestLogModule;
import ness.httpserver2.log.log4j.Log4jRequestLogModule;

import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.nesscomputing.config.Config;

public class HttpServerModule extends ServletModule
{
    /** Should match the value of the constant in tc-servlet GuiceListener for war-based apps. */
    public static final String CONTEXT_PATH_NAME = "_contextPath";

    private final Config config;

    public HttpServerModule(final Config config)
    {
        this.config = config;
    }

    @Override
    public void configureServlets()
    {
        final HttpServerConfig httpServerConfig = config.getBean(HttpServerConfig.class);
        bind(HttpServerConfig.class).toInstance(httpServerConfig);

        // Bind the legacy CONTEXT_PATH_NAME constants to the first path in the service. Do we actually still need that?
        bindConstant().annotatedWith(Names.named(CONTEXT_PATH_NAME)).to("");

        install (new LogFieldsModule());
        install (new FileRequestLogModule(config));
        install (new Log4jRequestLogModule(config));
//         install (new SyslogRequestLogModule(config));

        final Class<? extends HttpServer> serverClass = httpServerConfig.getServerClass();
        bind (serverClass).asEagerSingleton();
        bind (HttpServer.class).to(serverClass).asEagerSingleton();

        bind (Servlet.class).to(InvalidRequestServlet.class).in(Scopes.SINGLETON);
        bind (GuiceFilter.class).in(Scopes.SINGLETON);
    }
}
