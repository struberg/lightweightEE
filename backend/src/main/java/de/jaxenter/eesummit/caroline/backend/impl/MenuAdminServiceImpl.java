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

import de.jaxenter.eesummit.caroline.backend.api.MenuAdminService;
import de.jaxenter.eesummit.caroline.entities.MenuItem;
import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@ApplicationScoped
@Transactional
@Typed(MenuAdminService.class) // ATTENTION: otherwise this class would also act as 'MenuService' -> AmbiguousResolutionException
public class MenuAdminServiceImpl extends MenuServiceImpl implements MenuAdminService
{
    private @Inject EntityManager em;


    /**
     * This is the tree of all menu items.
     * It must later get filtered for roles.
     */
    private volatile List<MenuItem> allMenuItems = null;


    @Override
    protected EntityManager getEm()
    {
        return em;
    }

    @Override
    public MenuItem createMenuItem(int level, MenuItem parent, String resourceKey, int ordinal,
                                   String roles, String action)
    {
        MenuItem menuItem= new MenuItem();
        menuItem.setLevel(level);
        menuItem.setOrdinal(ordinal);
        menuItem.setRoles(roles);
        menuItem.setAction(action);
        menuItem.setResourceKey(resourceKey);

        // ATTENTION: we do a em.merge if needed -> reattach()
        menuItem.setParent(reattach(parent));

        em.persist(menuItem);

        return menuItem;
    }
}
