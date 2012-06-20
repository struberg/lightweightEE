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

import de.jaxenter.eesummit.caroline.backend.tools.TransactionAware;
import de.jaxenter.eesummit.caroline.entities.Employee;
import de.jaxenter.eesummit.caroline.entities.MenuItem;
import de.jaxenter.eesummit.caroline.backend.api.MenuService;
import de.jaxenter.eesummit.caroline.entities.CaroLineUser;
import org.apache.deltaspike.jpa.api.Transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@ApplicationScoped
public class MenuServiceImpl extends AbstractService<MenuItem> implements MenuService
{

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
    public MenuItem getMenu(CaroLineUser usr)
    {
        List<MenuItem> unfilteredMenu = getAllMenus();

        List<MenuItem> filteredMenu = filterMenu(unfilteredMenu, usr);

        // our outermost list only contains the root menu item...
        return getChildMenus(filteredMenu, null)[0];
    }

    /**
     * Recursively fill the menu
     */
    private MenuItem[] getChildMenus(List<MenuItem> filteredMenu, MenuItem parent)
    {
        List<MenuItem> children = new ArrayList<MenuItem>();
        for(MenuItem menuItem : filteredMenu)
        {
            if (menuItem.getParent() == parent)
            {
                menuItem.setChildren(getChildMenus(filteredMenu, menuItem));
                children.add(menuItem);
            }
        }

        if (children.size() == 0)
        {
            return null;
        }

        return children.toArray(new MenuItem[children.size()]);
    }

    /**
     * filter out all menus where we dont have access to.
     */
    private List<MenuItem> filterMenu(List<MenuItem> unfilteredMenu, CaroLineUser usr)
    {
        List<MenuItem> filteredMenu = new ArrayList<MenuItem>();

        boolean isAdmin = false;
        boolean isEmployee = false;
        boolean isCustomer = false;

        if (usr != null)
        {
            if (usr instanceof Employee)
            {
                isEmployee = true;
                isAdmin = ((Employee) usr).isAdmin();
            }
            else
            {
                isCustomer = true;
            }

        }

        for (MenuItem menuItem : unfilteredMenu)
        {
            List<String> roleList = menuItem.getRoleList();
            if (roleList != null && !roleList.isEmpty())
            {
                if (roleList.contains("admin") && !isAdmin)
                {
                    continue;
                }
                if (roleList.contains("employee") && !isEmployee)
                {
                    continue;
                }
                if (roleList.contains("customer") && !isCustomer)
                {
                    continue;
                }
            }
            filteredMenu.add(menuItem);
        }

        return filteredMenu;
    }

    public synchronized List<MenuItem> getAllMenus()
    {
        // we could also add a timeout via System.nanoTime()
        if (allMenuItems == null)
        {
            allMenuItems = self.loadRootMenu();
            // NOTE: allMenuItems now only contains DETACHED entities already!
        }

        return allMenuItems;
    }

    /**
     * ATTENTION: for getting any interceptors applied
     *      when calling a method inside the own class,
     *      we MUST invoke the method via the proxy!
     * unfiltered list of all menus
     */
    private @Inject MenuServiceImpl self;
    private @Inject @TransactionAware EntityManager em;

    @Transactional(qualifier = TransactionAware.class)
    public List<MenuItem> loadRootMenu()
    {
        // we order immediately to not have to deal with it later
        Query q = em.createQuery("SELECT m FROM MenuItem m ORDER BY m.ordinal ASC", MenuItem.class);
        return q.getResultList();
    }
}
