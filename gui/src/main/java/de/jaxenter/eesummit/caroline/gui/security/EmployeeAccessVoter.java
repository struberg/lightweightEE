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
package de.jaxenter.eesummit.caroline.gui.security;

import de.jaxenter.eesummit.caroline.gui.beans.User;
import org.apache.myfaces.extensions.cdi.core.api.security.AbstractAccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import java.util.Set;

/**
 * access decission voter for admin pages.
 */
@ApplicationScoped
public class EmployeeAccessVoter extends AbstractAccessDecisionVoter
{

    private static final long serialVersionUID = -3321616879108078874L;

    private @Inject @Jsf MessageContext messageContext;
    private @Inject User user;

    public void checkPermission(InvocationContext invocationContext, Set<SecurityViolation> violations)
    {
        if (!user.isLoggedIn() && !user.isEmployee())
        {
            String reason = this.messageContext.message().text("{user_violation_admin}").toText();
            violations.add(newSecurityViolation(reason));
        }
    }

}
