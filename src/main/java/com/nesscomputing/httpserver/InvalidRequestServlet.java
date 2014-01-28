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

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

import com.nesscomputing.logging.Log;

/**
 * This is the backing servlet for Filter chains if no "default mapping" is configured. It will log any attempts to get Data out of it.
 */
@Singleton
class InvalidRequestServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private static final Log LOG = Log.findLog();

    @Override
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException
    {
        LOG.warn("Invalid request: %s %s!", req.getMethod(), req.getRequestURI());
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain");
        final Writer w = res.getWriter();
        w.write(String.format("Invalid request to %s '%s'", req.getMethod(), req.getRequestURI()));
    }
}
