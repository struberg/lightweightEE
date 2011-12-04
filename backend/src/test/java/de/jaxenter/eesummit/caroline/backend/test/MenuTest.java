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
package de.jaxenter.eesummit.caroline.backend.test;

import de.jaxenter.eesummit.caroline.backend.api.MenuService;
import de.jaxenter.eesummit.caroline.backend.api.MenuAdminService;
import de.jaxenter.eesummit.caroline.entities.MenuItem;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Test the {@link MenuService} and {@link MenuAdminService}
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class MenuTest extends CarolineTest
{
    private @Inject MenuService menuSvc;
    private @Inject MenuAdminService menuAdminSvc;


    @BeforeClass
    public void cleanUpDb() throws Exception
    {
        EntityManager em = BeanManagerProvider.getInstance().getContextualReference(EntityManager.class);
        em.getTransaction().begin();

        // otherwise we will get problems with the parent contrainst when deleting all menus!
        Query update = em.createQuery("UPDATE MenuItem AS m set m.parent = null", MenuItem.class);
        update.executeUpdate();

        // now we can easily delete all our menu items, because they have no active parent contraints anymore
        Query delete = em.createQuery("DELETE from MenuItem AS m", MenuItem.class);
        delete.executeUpdate();

        em.getTransaction().commit();
    }

    /**
     * This unit test creates our menu!
     *
     */
    @Test(groups = "createData")
    public void testMenuCreation() throws Exception
    {
        // phase 1: create the menu structure
        {
            MenuItem rootMenu = menuAdminSvc.createMenuItem(0, null, "ROOT", 1, null, null);

            // create the admin menu part
            MenuItem adminMenu = menuAdminSvc.createMenuItem(1, rootMenu, "MENU_ADMIN", 30, "admin",
                                     "/admin/overview.xhtml");
            MenuItem adminEmployee = menuAdminSvc.createMenuItem(2, adminMenu, "MENU_ADMIN_EMPLOYEE", 1, "admin",
                                     "/admin/employee.xhtml");
            MenuItem adminWarehouse = menuAdminSvc.createMenuItem(2, adminMenu, "MENU_ADMIN_WAREHOUSE", 2, "admin",
                                     "/admin/warehouse.xhtml");

            // create the employee menu part
            MenuItem employeeMenu = menuAdminSvc.createMenuItem(1, rootMenu, "MENU_EMP", 1, "employee",
                                     "/employee/overview.xhtml");
            MenuItem employeeCustomer = menuAdminSvc.createMenuItem(2, employeeMenu, "MENU_EMP_CUSTOMER", 1, "employee",
                                     "/employee/searchCustomer.xhtml");


            // create the customer menu part
            MenuItem customerMenu = menuAdminSvc.createMenuItem(1, rootMenu, "MENU_CUST", 2, "customer",
                                     "/customer/overview.xhtml");
        }

    }
}
