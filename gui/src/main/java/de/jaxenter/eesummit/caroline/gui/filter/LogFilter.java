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
package de.jaxenter.eesummit.caroline.gui.filter;



import de.jaxenter.eesummit.caroline.gui.beans.UserController;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * <p>LogFilter logs start and end of each Servlet Requests
 * and prints out the elapsed time.</p>
 *
 * <p>We also set the Encoding to UTF-8.</p>
 * <p>You can also set the cluster node id with cluster.node</p>
 *
 * <p>configure web.xml like this:</p>
 * <pre>
 *     &lt;filter&gt;
 *         &lt;filter-name&gt;LogFilter&lt;/filter-name&gt;
 *         &lt;filter-class&gt;de.jaxenter.eesummit.caroline.gui.filter.LogFilter&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;dropurl.0&lt;/param-name&gt;
 *             &lt;param-value&gt;sampleurl_to_ignore&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 *
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;LogFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 * </pre>
 *
 */
public class LogFilter implements Filter {

    public static final String REQUEST_CURRENT_VIEW_URI = "requestCurrentViewURI";

    private static final String REQUEST_ENCODING = "UTF-8";
    private static final boolean FORCE_REQUEST_ENCODING = true;

    private static final String MDC_SESSION = "sessionId";
    private static final String MDC_USER = "userId";
    private static final String MDC_NODE = "nodeId";

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    /**
     * Cluster node id. Important info if syslogd oder similar gets used.
     */
    private String nodeId = null;

    /**
     * prevent logging of the following URLs
     */
    private List<String> dropUrls = null;

    private ThreadMXBean threadBean;

    private @Inject UserController user;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String remoteAddress = null;
        String sessionId = null;
        String userId = null;

        long start = 0;
        long startCpu = 0;
        Throwable throwable = null;
        boolean dropped = false;

        String url = "";
        String method = "";
        String agent = null;

        HttpServletRequest httpServletRequest = null;
        HttpServletResponse res = null;

        try {
            Validate.isTrue(servletRequest instanceof HttpServletRequest, "filter oops?");
            httpServletRequest = (HttpServletRequest) servletRequest;
            Validate.isTrue(servletResponse instanceof HttpServletResponse, "filter oops?");
            res = (HttpServletResponse) servletResponse;

            if (httpServletRequest.getCharacterEncoding() == null || FORCE_REQUEST_ENCODING) {
                httpServletRequest.setCharacterEncoding(REQUEST_ENCODING);
                res.setCharacterEncoding(REQUEST_ENCODING);
            }

            url = httpServletRequest.getRequestURI();
            method = httpServletRequest.getMethod();
            String qs = httpServletRequest.getQueryString();
            if (qs != null)
            {
                url += "?" + qs;
            }

            agent = httpServletRequest.getHeader("User-Agent");

            if (user != null)
            {
                userId = user.getLogin();
            }

            dropped = isDroppedUrl(url);

            if (!dropped) {
                // logged details about this very Request
                StringBuilder msg = new StringBuilder(100);

                remoteAddress = getRemoteAddress(httpServletRequest);

                setupMdc(sessionId, userId);

                msg.append("request start ").append(method).append(' ').append(url)
                        .append(" remote address:").append(remoteAddress)
                        .append(" UA=").append(agent);
                log.info(msg.toString());

                if (threadBean != null) {
                    startCpu = threadBean.getCurrentThreadCpuTime();
                }
                start = System.nanoTime();
            }

            // continue with the rest of the request chain
            filterChain.doFilter(servletRequest, servletResponse);
        }
        catch (IOException e) {
            throwable = e;
            throw e;
        }
        catch (ServletException e) {
            if (e.getRootCause() != null) {
                throwable = e.getRootCause();
            }
            else {
                throwable = e;
            }
            throw e;
        }
        catch (Throwable e) {  // be sure to get all errors
            throwable = e;
            throw new ServletException(e);
        }
        finally {
            if (!dropped) {
                String viewUri = (String) httpServletRequest.getAttribute(REQUEST_CURRENT_VIEW_URI);
                long cpuTime = 0;
                if (threadBean != null) {
                    cpuTime = threadBean.getCurrentThreadCpuTime() - startCpu;
                }
                long time = System.nanoTime() - start;

                int statusCode = res.getStatus();

                StringBuilder msg = new StringBuilder(100);

                msg.append("request done ").append(url);
                if (viewUri != null) {
                    msg.append(" view=").append(viewUri);
                }
                msg.append(" code=").append(statusCode).
                        append(" time=").append(formatNanos(time)).append("ms");
                if (threadBean != null) {
                    msg.append(" cpu=").append(formatNanos(cpuTime)).append("ms");
                }

                if (throwable == null) {
                    if (statusCode >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                        // switch to error in case of code 500 or higher
                        log.error(msg.toString());
                    }
                    else {
                        log.info(msg.toString());
                    }
                }
                else {
                    msg.append(" ex=").append(throwable.getClass().getSimpleName());
                    msg.append(" msg=").append(throwable.getMessage());
                    // also log agent in error/warning case
                    msg.append(" UA=").append(agent);

                    log.error(msg.toString());

                    // log all (post) parameters in case of warn/error
                    Enumeration<String> params = httpServletRequest.getParameterNames();
                    StringBuffer paramsStr = new StringBuffer();
                    while (params.hasMoreElements()) {
                        String name = params.nextElement();
                        paramsStr.append(name).append("=").append(httpServletRequest.getParameter(name)).append(" ");
                    }
                    log.info("params: " + paramsStr);
                }

            }

            removeMDC();
        }
    }

    private boolean isDroppedUrl(String url) {
        // does any stopUrl match url
        for (String stopUrl : dropUrls) {
            if (url.contains(stopUrl)) {
                return true;
            }
        }

        return false;
    }

    private void setupMdc(String sessionId, String userId) {

        MDC.put(MDC_SESSION, sessionId != null ? sessionId : "");
        MDC.put(MDC_USER, userId != null ? userId : "");
        MDC.put(MDC_NODE, nodeId != null ? nodeId : "local");
    }

    private void removeMDC() {
        MDC.remove(MDC_SESSION);
        MDC.remove(MDC_USER);
        MDC.remove(MDC_NODE);
    }

    private String formatNanos(long nanos) {
        // show millis with 3 digits (i.e. us)
        return new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(nanos / 1000000.0);
    }

    private String getRemoteAddress(HttpServletRequest servletRequest) {
        String forwarded = servletRequest.getHeader("X-Forwarded-For");
        if (forwarded != null) {
            return forwarded;
        }
        else {
            return servletRequest.getRemoteAddr();
        }
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        nodeId = System.getProperty("cluster.node");
        if (nodeId == null) {
            try {
                nodeId = InetAddress.getLocalHost().getHostName();
                if (nodeId.contains(".")) {
                    // strip domain if any (e.g. myserver.mydomain.com -> myserver)
                    nodeId = nodeId.split("\\.")[0];
                }
            }
            catch (Exception e) {
                log.warn("failed to read host name.", e);
            }
        }

        dropUrls = new ArrayList<String>();
        int i = 0;
        String dropUrlParam;
        while ((dropUrlParam = filterConfig.getInitParameter("dropurl." + i)) != null) {
            log.info("Adding dropurl." + i + " " + dropUrlParam);
            dropUrls.add(dropUrlParam);
            i++;
        }

        threadBean = ManagementFactory.getThreadMXBean();
    }


    @Override
    public void destroy() {
        // nothing to do
    }
}
