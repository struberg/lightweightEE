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
package de.jaxenter.eesummit.caroline.gui.beans.admin;

import de.jaxenter.eesummit.caroline.backend.api.CustomerService;
import de.jaxenter.eesummit.caroline.entities.Customer;
import de.jaxenter.eesummit.caroline.gui.viewconfig.AdminPages;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backing bean for creating a new user.
 */
@Named
@ViewAccessScoped
public class EditCustomerView implements Serializable
{

    private
    @Inject
    CustomerService custSvc;
    private
    @Inject
    @Jsf
    MessageContext messageContext;


    private Long customerId;
    private Customer customer;


    private Integer provision;


    public Class<? extends ViewConfig> saveCustomer()
    {
        if (isEdit())
        {
            //X custSvc.storeCustomer(customerId, firstName, lastName, msisdn, cardId, dayLimit, cardLocked);
        }
        else
        {
            //X custSvc.createCustomer(firstName, lastName, msisdn, cardId);
        }
        /*X
        catch (CaroLineException be) {
            Message msg = messageContext.message().text("{user_preferences_stored}").
                                                   payload(MessageSeverity.INFO).create();
            messageContext.addMessage(msg);
            return null;
        }
        */

        return AdminPages.Overview.class;
    }

    public Class<? extends ViewConfig> editCustomer(Customer c)
    {
        if (c != null)
        {
            customerId = c.getId();
            customer = c;
        }

        return AdminPages.EditCustomer.class;
    }

    public Class<? extends ViewConfig> cancel()
    {
        return AdminPages.Overview.class;
    }

    public boolean isEdit()
    {
        return customerId != null;
    }

}
