/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.jaxenter.eesummit.caroline.gui.redirect;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.application.ViewExpiredException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>Sadly we need to do the redirect via a servlet because
 * JSP doesn't work in tomcat-6 because we have ExpressionLanguage-2.2
 * configured for our JSF facelets.</p>
 *
 * <p>it will take the parameter <code>dest</code> and perform a
 * client side redirect to it.</p>
 *
 * <p>A redirect is currently always servlet relative!</p>
 *
 * @author Mark Struberg
 */
public class RedirectServlet extends HttpServlet {

    public final static String REDICRECT_DEST_PARAM = "dest";
    public final static String REDICRECT_DONE_PARAM = "redirected";

    private static Set<String> allowedTypes = new HashSet<String>();
    static {
        allowedTypes.add(ViewExpiredException.class.getName());
        allowedTypes.add(NonexistentConversationException.class.getName());
    }

    private Logger log;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log = Logger.getLogger(getClass().getName());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String baseUrl = request.getParameter(REDICRECT_DEST_PARAM);

        if (baseUrl == null || baseUrl.length() < 1) {
            throw new ServletException("could not find dest parameter!");
        }

        // Retrieve the possible error attributes
        Object statusObj = request.getAttribute("javax.servlet.error.status_code");
        String status = statusObj != null ? statusObj.toString() : null;

        Object messageObj = request.getAttribute("javax.servlet.error.message");
        String message = messageObj != null ? messageObj.toString() : null;

        Object typeObj = request.getAttribute("javax.servlet.error.exception_type");
        String type = typeObj != null ? ((Class<? extends Throwable>) typeObj).getName() : null;

        String uri = (String) request.getAttribute("javax.servlet.error.request_uri");

        StringBuilder msg = new StringBuilder();
        msg.append("got redirect after");
        msg.append(" uri=").append(uri);
        msg.append(" status=").append(status);
        msg.append(" error=").append(message);

        // it's important to know what resources are missing!
        log.warning(msg.toString());

        // help us from getting into cyclic redirects
        String alreadyRedirected = request.getParameter(REDICRECT_DONE_PARAM);
        if (alreadyRedirected != null) {
            // let's do nothing
            return;
        }
        // a redirect is currently always servlet relative
        StringBuilder dest = new StringBuilder(request.getRequestURL().toString().replace("redirect", baseUrl));

        addUrlParameter(dest, REDICRECT_DONE_PARAM, "true", (baseUrl.contains("?") ? "&" : "?"));
        if (allowedTypes.contains(type)) {
            addUrlParameter(dest, "errorCode", type, "&");
        }
        else if (status != null) {
            addUrlParameter(dest, "errorCode", status, "&");
        }

        response.sendRedirect(dest.toString());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void addUrlParameter(StringBuilder url, String name, String value, String separator) {
        url.append(separator).append(name).append("=").append(value);
    }

}
