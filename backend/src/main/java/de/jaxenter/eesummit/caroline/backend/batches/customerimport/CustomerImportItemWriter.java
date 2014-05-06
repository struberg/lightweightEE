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
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import de.jaxenter.eesummit.caroline.backend.api.CustomerService;
import de.jaxenter.eesummit.caroline.entities.Customer;
import org.apache.batchee.extras.typed.NoStateTypedItemWriter;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Named
@Dependent
@Transactional  // needed as we do not use JTA in this project
public class CustomerImportItemWriter extends NoStateTypedItemWriter<Customer>
{
    private @Inject CustomerService customerService;

    @Override
    protected void doWriteItems(List<Customer> customers)
    {
        for (Customer c : customers)
        {
            customerService.createCustomer(c);
        }
    }
}
