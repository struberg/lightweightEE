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
package de.jaxenter.eesummit.caroline.gui.beans.employee;


import de.jaxenter.eesummit.caroline.backend.api.CustomerService;
import de.jaxenter.eesummit.caroline.entities.Customer;
import de.jaxenter.eesummit.caroline.gui.msg.CarolineMessages;
import de.jaxenter.eesummit.caroline.gui.viewconfig.EmployeePages;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.apache.deltaspike.jsf.api.message.JsfMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backing bean for creating and editing customers.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@ViewAccessScoped
@Named("editCustomer")
public class EditCustomerView implements Serializable
{
    private static final Logger logger = LoggerFactory.getLogger(EditCustomerView.class);

    private Customer customer = new Customer();
    private Long customerId = null;
    private boolean edit = false;

    private @Inject CustomerService customerSvc;
    private @Inject JsfMessage<CarolineMessages> messages;


    /**
     * This gets called from the view event.
     * see editCustomer.xhtml
     */
    public void init(ComponentSystemEvent ev)
    {
        if (customerId != null)
        {
            logger.info("loading customer with id {}", customerId);

            customer = customerSvc.getById(customerId);
            if (customer == null)
            {
                messages.addError().customerDoesNotExist(customerId);
            }
            edit = true;
        }
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Class<? extends ViewConfig> saveCustomer()
    {
        if (!edit)
        {
            customer = customerSvc.createCustomer(customer);
            customerId = customer.getId();
        }
        else
        {
            customerSvc.save(customer);
        }

        return EmployeePages.EditCustomer.class;
    }

    public boolean isEdit()
    {
        return edit;
    }
}
