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

import de.jaxenter.eesummit.caroline.gui.beans.UserController;
import de.jaxenter.eesummit.caroline.gui.msg.CarolineMessages;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext;
import org.apache.deltaspike.security.api.authorization.DefaultSecurityViolation;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * access decission voter for admin pages.
 */
@ApplicationScoped
public class AdminAccessVoter implements AccessDecisionVoter
{
    private static final long serialVersionUID = -3321616879108078874L;

    private @Inject CarolineMessages messages;
    private @Inject UserController user;


    @Override
    public Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext)
    {

        if (!user.isLoggedIn() && !user.isEmployee())
        {
            String reason = messages.loginRequiredEmployee();
            Set<SecurityViolation> violations = new HashSet<SecurityViolation>();
            violations.add(new DefaultSecurityViolation(messages.loginRequired()));
            return violations;
        }

        return Collections.emptySet();
    }

}
