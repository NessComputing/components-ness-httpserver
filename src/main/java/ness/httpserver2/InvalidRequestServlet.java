package ness.httpserver2;

import com.nesscomputing.logging.Log;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the backing servlet for Filter chains if no "default mapping" is configured. It will log any attempts to get Data out of it.
 */
class InvalidRequestServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.findLog();

    @Override
    public void service(final HttpServletRequest req, final HttpServletResponse res)
    {
        LOG.warn("Request to %s '%s' served by this servlet!", req.getMethod(), req.getRequestURI());
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain");
        try {
            final Writer w = res.getWriter();
	        w.write(String.format("Invalid request to %s '%s'", req.getMethod(), req.getRequestURI()));
	        w.flush();
        } catch (IOException e) {
        	LOG.error(e, "Couldn't write response");
        }
    }
}
