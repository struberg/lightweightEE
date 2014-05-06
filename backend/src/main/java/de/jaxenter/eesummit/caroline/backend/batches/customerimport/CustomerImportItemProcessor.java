/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jaxenter.eesummit.caroline.backend.batches.customerimport;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import de.jaxenter.eesummit.caroline.entities.Customer;
import org.apache.batchee.extras.typed.TypedItemProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Named
@Dependent
public class CustomerImportItemProcessor extends TypedItemProcessor<CustomerCsv, Customer>
{
    private static final Logger logger = LoggerFactory.getLogger(CustomerImportItemProcessor.class);

    @Override
    protected Customer doProcessItem(CustomerCsv customerCsv)
    {
        if (StringUtils.isEmpty(customerCsv.getFirstName()) || StringUtils.isEmpty(customerCsv.getLastName()))
        {
            logger.warn("Skipping import of user " + customerCsv.toString());
            return null;
        }

        Customer customer = new Customer();
        customer.setFirstName(customerCsv.getFirstName());
        customer.setLastName(customerCsv.getLastName());

        calculateLoginCredentials(customer);

        return customer;
    }

    private void calculateLoginCredentials(Customer customer)
    {
        String loginId = customer.getFirstName().toLowerCase() + "." + customer.getLastName().toLowerCase();
        customer.setLoginId(loginId);
        customer.setLoginHash(null); // user cannot login himself yet. Needs to get activated manually
    }
}
