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


import de.jaxenter.eesummit.caroline.backend.api.CustomerService;
import de.jaxenter.eesummit.caroline.backend.tools.QueryBuilder;
import de.jaxenter.eesummit.caroline.entities.Customer;
import org.apache.deltaspike.jpa.api.transaction.Transactional;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Implementation of the CustomerService.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@ApplicationScoped
@Transactional
public class CustomerServiceImpl extends AbstractService<Customer> implements CustomerService
{
    private @Inject EntityManager em;


    @Override
    protected EntityManager getEm()
    {
        return em;
    }

    @Override
    public boolean isCustomerActive(Long customerId)
    {
        Customer customer = em.find(Customer.class, customerId);
        if (customer != null)
        {
            customer.isActive();
        }

        return false;
    }

    @Override
    public Customer createCustomer(Customer c)
    {
        c.setActive(true);
        return create(c);
    }

    @Override
    public List<Customer> getCustomers()
    {
        return searchCustomers(null, null);
    }

    @Override
    public List<Customer> searchCustomers(String lastName, String firstName)
    {
        QueryBuilder qb = new QueryBuilder("SELECT c from Customer AS c");
        
        if (lastName != null && lastName.length()>0)
        {
            qb.addQueryPart("c.lastName like :lastName", "lastName", lastName.replace('*', '%'));
        }
        if (firstName != null && firstName.length()>0)
        {
            qb.addQueryPart("c.firstName like :firstName", "firstName",firstName.replace('*', '%'));
        }

        Query q = qb.getQuery(em);

        return q.getResultList();
    }
}
