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

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/*
  configure web.xml like this:

    <filter>
        <filter-name>LogFilter</filter-name>
        <filter-class>de.jaxenter.eesummit.caroline.gui.filter.LogFilter</filter-class>
        <init-param>
            <param-name>ndc</param-name>
            <param-value>session,address</param-value>
        </init-param>
        <init-param>
            <param-name>dropurl.0</param-name>
            <param-value>javax.faces.resource</param-value>
        </init-param>
        <init-param>
            <param-name>dropurl.1</param-name>
            <param-value>primefaces_resource</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>LogFilter</filter-name>
        <url-pattern>*.xhtml</url-pattern>
    </filter-mapping>

*/

/**
 * servlet filter that logs all requests and sets sessionId as NDC and sets request encoding
 */
public class LogFilter implements Filter
{

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    private List<String> dropUrls = null;

    private String requestEncoding = "UTF-8";
    private boolean forceRequestEncoding = false;

    private boolean ndcEnabled;
    private boolean ndcSession;
    private boolean ndcAddress;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException
    {
        String remoteAddress = null;
        String sessionId = null;
        String uid = "0";

        long start = 0;
        String url = "";
        String method = "";
        Throwable throwable = null;
        boolean dropped = false;
        String agent = null;
        try
        {
            Validate.isTrue(request instanceof HttpServletRequest, "filter oops?");
            HttpServletRequest req = (HttpServletRequest) request;

            if (req.getCharacterEncoding() == null || forceRequestEncoding)
            {
                req.setCharacterEncoding(requestEncoding);
            }

            url = req.getRequestURI();
            method = req.getMethod();
            String qs = req.getQueryString();
            agent = req.getHeader("User-Agent");
            if (qs != null)
            {
                url += "?" + qs;
            }

            for (String stopUrl : dropUrls)
            {  // does any stopUrl match url
                if (url.indexOf(stopUrl) != -1)
                {
                    dropped = true;
                    break; // stop searching
                }
            }

            if (!dropped)
            {
                if (ndcEnabled)
                {
                    if (ndcAddress)
                    {
                        String forwarded = req.getHeader("X-Forwarded-For");
                        if (forwarded != null)
                        {
                            remoteAddress = forwarded;
                        }
                        else
                        {
                            remoteAddress = request.getRemoteAddr();
                        }
                    }
                    if (ndcSession)
                    {
                        HttpSession session = req.getSession(false); // do not create
                        if (session != null)
                        {
                            sessionId = session.getId();
                            String sessOID = (String) session.getAttribute("USER_ID_LOG");
                            uid = sessOID == null ? "0" : sessOID;
                        }
                    }
                }
                StringBuilder msg = simulateNDC(remoteAddress, sessionId, uid);
                msg.append("request start ").append(method).append(" ").append(url).append(" UA=").append(agent);
                logger.info(msg.toString());
                start = System.currentTimeMillis();
            }

            filterChain.doFilter(request, response);
        }
        catch (IOException e)
        {
            throwable = e;
            throw e;
        }
        catch (ServletException e)
        {
            if (e.getRootCause() != null)
            {
                throwable = e.getRootCause();
            }
            else
            {
                throwable = e;
            }
            throw e;
        }
        catch (Throwable e)
        {  // be sure to get all errors
            throwable = e;
            throw new ServletException(e);
        }
        finally
        {
            if (!dropped)
            {
                long time = System.currentTimeMillis() - start;
                StringBuilder msg = simulateNDC(remoteAddress, sessionId, uid);
                msg.append("request done ").append(method).append(" ");
                msg.append(url).append(" time=").append(time).append("ms");

                if (throwable == null)
                {
                    logger.info(msg.toString());
                }
                else
                {
                    String name = throwable.getClass().getSimpleName();
                    msg.append(" ex=").append(name);
                    msg.append(" msg=").append(throwable.getMessage());
                    if (name.equals("ViewExpiredException") || name.equals("ClientAbortException"))
                    {
                        logger.warn(msg.toString());
                    }
                    else
                    {
                        msg.append(" UA=").append(agent);  // also log agent in error case
                        logger.error(msg.toString());
                    }
                }
            }
        }
    }

    @Override
    public void destroy()
    {
        // no action needed
    }

    // build fake prefix like NDC. (at least for start/done trace)
    private StringBuilder simulateNDC(String remoteAddress, String sessionId, String uid)
    {
        StringBuilder msg = new StringBuilder();
        if (uid != null)
        {
            msg.append(uid).append(" ");
        }
        if (remoteAddress != null)
        {
            msg.append(remoteAddress).append(" ");
        }
        if (sessionId != null)
        {
            msg.append(sessionId).append(" ");
        }
        return msg;
    }

    @Override
    public void init(FilterConfig config) throws ServletException
    {

        dropUrls = new ArrayList<String>();
        int idx = 0;
        String dropUrlParam;
        while ((dropUrlParam = config.getInitParameter("dropurl." + idx)) != null)
        {
            logger.info("adding dropUrl " + idx + ": " + dropUrlParam);
            dropUrls.add(dropUrlParam);
            idx++;
        }

        String ndcParam = config.getInitParameter("ndc");

        ndcEnabled = false;
        if (ndcParam != null)
        {
            logger.info("NDC enabled: " + ndcParam);
            ndcEnabled = true;
            ndcSession = ndcParam.indexOf("session") != -1;
            ndcAddress = ndcParam.indexOf("address") != -1;
        }

    }

}
