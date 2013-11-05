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
package de.jaxenter.eesummit.caroline.gui.beans;

import de.jaxenter.eesummit.caroline.backend.api.UserService;
import de.jaxenter.eesummit.caroline.entities.CaroLineUser;
import de.jaxenter.eesummit.caroline.entities.Customer;
import de.jaxenter.eesummit.caroline.entities.Employee;
import de.jaxenter.eesummit.caroline.gui.msg.CarolineMessages;
import de.jaxenter.eesummit.caroline.gui.viewconfig.EmployeePages;
import de.jaxenter.eesummit.caroline.gui.viewconfig.PublicPages;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.jsf.api.message.JsfMessage;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

/**
 * Backing bean which represents a user.
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@SessionScoped
@Named("user")
public class UserController implements Serializable
{
    private boolean loggedIn = false;

    private Long userId;
    private boolean admin = false;
    private boolean customer = false;
    private boolean employee = false;

    private String viewName;

    private String login;
    private String password;

    /** could change dynamically */
    private Locale locale = Locale.GERMAN;


    private @Inject JsfMessage<CarolineMessages> messages;

    private @Inject UserService usrSvc;

    /** we send this to indicate a user settings change */
    private @Inject Event<UserSettingsChangedEvent> userSettingsChangedEvent;


    private CaroLineUser usr;

    /**
     * log the user in.
     * @return the target page
     */
    public Class<? extends ViewConfig> login()
    {
        authenticate();

        userSettingsChangedEvent.fire(new UserSettingsChangedEvent());

        if (isCustomer())
        {
            return PublicPages.Customer.class;
        }
        else if (isEmployee())
        {
            return EmployeePages.Overview.class;
        }

        // fire an error message
        messages.addError().unknownUser();

        return null;
    }

    public Class<? extends ViewConfig> logout()
    {
        usr = null;
        loggedIn = false;
        userId = null;
        admin = false;
        customer = false;
        employee = false;
        viewName = null;
        login = null;
        password = null;

        userSettingsChangedEvent.fire(new UserSettingsChangedEvent());

        return PublicPages.Login.class;
    }

    private void authenticate()
    {
        // first reset all values
        loggedIn = false;
        customer = false;
        admin    = false;
        viewName = null;

        // perform the actual login
        usr = usrSvc.login(login, password);

        if (usr != null)
        {
            userId = usr.getId();
            loggedIn = true;
            viewName = usr.getFirstName() + " " + usr.getLastName();

            if (usr instanceof Customer)
            {
                customer = true;
                admin = false;
            }
            else if (usr instanceof Employee)
            {
                Employee emp = (Employee) usr;
                admin = emp.isAdmin();
                employee = true;
            }
        }
    }

    public CaroLineUser getUser()
    {
        return usr;
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }

    public boolean isEmployee()
    {
        return employee;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public boolean isCustomer()
    {
        return customer;
    }

    public String getViewName()
    {
        return viewName;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Long getUserId()
    {
        return userId;
    }

    public Customer getCustomer()
    {
        if (isCustomer())
        {
            return (Customer) usr;
        }
        return null;
    }

    public Locale getLocale()
    {
        return locale;
    }
}
