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
package de.jaxenter.eesummit.caroline.backend.impl;


import de.jaxenter.eesummit.caroline.backend.api.UserService;
import de.jaxenter.eesummit.caroline.entities.CaroLineUser;
import org.apache.commons.lang.Validate;
import org.apache.deltaspike.jpa.api.Transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Implementation of the UserService.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@ApplicationScoped
@Transactional
public class UserServiceImpl extends AbstractService<CaroLineUser> implements UserService
{

    private
    @Inject
    EntityManager em;

    @Override
    protected EntityManager getEm()
    {
        return em;
    }


    @Override
    public String getPasswordHash(String password)
    {
        //X TODO make hash of it
        return password;
    }


    @Override
    public CaroLineUser login(String loginId, String password)
    {
        Validate.notNull(loginId);
        Validate.notNull(password);

        String pwdHash = getPasswordHash(password);

        Query q = em.createQuery("select u from CaroLineUser as u "
                + "where u.loginId = :loginId and u.loginHash = :loginHash",
                CaroLineUser.class);
        q.setParameter("loginId", loginId);
        q.setParameter("loginHash", pwdHash);

        CaroLineUser usr = null;

        try
        {
            usr = (CaroLineUser) q.getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }

        return usr;
    }

}
