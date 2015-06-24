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

import de.jaxenter.eesummit.caroline.backend.api.EmployeeService;
import de.jaxenter.eesummit.caroline.backend.api.UserService;
import de.jaxenter.eesummit.caroline.entities.CaroLineUser;
import de.jaxenter.eesummit.caroline.entities.Employee;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@RunWith(CdiTestRunner.class)
public class EmployeeServiceTest
{
    public static final String TEST_PREFIX = "TEST_EMP_";

    private @Inject EmployeeService employeeSvc;
    private @Inject UserService usrSvc;
    private @Inject CleanUp cleanUp;

    @Before
    public void cleanUpDb() throws Exception
    {
        cleanUp.cleanUpDb();
    }

    @ApplicationScoped
    @Transactional
    public static class CleanUp
    {
        private @Inject EntityManager em;

        public void cleanUpDb() throws Exception
        {
            Query q = em.createQuery("DELETE from Employee AS e where e.lastName LIKE :name", Employee.class);
            q.setParameter("name", TEST_PREFIX + "%");
            q.executeUpdate();
        }
    }

    /**
     * This unit test covers the whole lifecycle of a user creation.
     */
    @Test
    public void testEmployeeCreation() throws Exception {

        // step 1: create the emplyoees
        {
            Employee emp1 = new Employee();
            emp1.setActive(true);
            emp1.setFirstName("Karl");
            emp1.setLastName(TEST_PREFIX +"Maier");
            emp1.setLoginId("karl");
            emp1.setAdmin(true);
            emp1.setLoginHash(usrSvc.getPasswordHash("karl"));

            employeeSvc.createEmployee(emp1);

            Employee emp2 = new Employee();
            emp2.setActive(true);
            emp2.setFirstName("Klara");
            emp2.setLastName(TEST_PREFIX + "Müller");
            emp2.setLoginId("klara");
            emp2.setLoginHash(usrSvc.getPasswordHash("klara"));

            employeeSvc.createEmployee(emp2);
        }

        // try to login with a wrong user
        {
            CaroLineUser usr = usrSvc.login("unknownUserId", "1111");
            Assert.assertNull(usr);
        }

        // try to login with a wrong password
        {
            CaroLineUser usr = usrSvc.login("karl", "nixda");
            Assert.assertNull(usr);
        }

        // step 2: login
        {
            CaroLineUser usr = usrSvc.login("karl", "karl");
            Assert.assertNotNull(usr);
            Assert.assertTrue(usr.isActive());
            Assert.assertTrue(usr instanceof Employee);
        }
    }
}
