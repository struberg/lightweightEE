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
package de.jaxenter.eesummit.caroline.backend.api;


import de.jaxenter.eesummit.caroline.entities.Customer;

import java.util.List;


/**
 * Service to handle creation and maintenance of customers.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public interface CustomerService extends GenericService<Customer>
{
    /**
     * @param customerId
     * @return <code>true</code> if the customer exists and is active, <code>false</code> otherwise.
     */
    public boolean isCustomerActive(Long customerId);

    /**
     * Used for the admin application
     *
     * @return all customers in the database
     */
    public List<Customer> getCustomers();

    /**
     * @param lastName or <code>null</code> for all
     * @param firstName or <code>null</code> for all
     * @return all customers with the given lastName and firstName
     */
    public List<Customer> searchCustomers(String lastName, String firstName);

    /**
     * Create a customer
     */
    public Customer createCustomer(Customer c);

}
