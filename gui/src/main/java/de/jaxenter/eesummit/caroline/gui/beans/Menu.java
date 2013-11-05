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

import de.jaxenter.eesummit.caroline.backend.api.MenuService;
import de.jaxenter.eesummit.caroline.entities.MenuItem;
import de.jaxenter.eesummit.caroline.gui.msg.CarolineMessages;
import org.apache.deltaspike.core.api.message.MessageContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * This menu is self-made for demonstration purpose.
 *
 * You could also use PrimeFaces {@link org.primefaces.model.MenuModel},
 * or the Menu from Trinidad or RichFaces.
 *
 * @see PrimeMenu for the PrimeFaces version of the menu
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@SessionScoped
@Named
public class Menu implements Serializable
{
    private String activeMenuItem;
    private MenuItem topLevelMenu;
    private String servletPath;

    private @Inject MenuService menuSvc;
    private @Inject UserController usr;
    private @Inject MessageContext messageContext;


    @PostConstruct
    public void init()
    {
        loadMenu();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        servletPath = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName()
                      + ":" + externalContext.getRequestServerPort() + externalContext.getRequestContextPath();
    }

    private void loadMenu()
    {
        topLevelMenu = menuSvc.getMenu(usr.getUser());
    }

    public String getActiveMenuItem() {
        return activeMenuItem;
    }

    public void activateMenuItem(String activeMenuItem) {
        this.activeMenuItem = activeMenuItem;
    }

    public String getText(MenuItem menuItem)
    {
        return messageContext.clone().messageSource(CarolineMessages.class.getName())
                             .message().template(menuItem.getResourceKey()).toString();
    }

    public String getAction(MenuItem menuItem)
    {
        String action = menuItem.getAction();

        if (action != null && (action.startsWith("http://") || action.startsWith("https://")))
        {
            // already a fully qualified URL
            return action;
        }
        return servletPath + action;
    }

    public MenuItem getTopLevelMenu()
    {
        return topLevelMenu;
    }

    /**
     * If the user logs in/out etc, then we need to reload the menu
     */
    protected void onUserSettingsChanged(@Observes UserSettingsChangedEvent usc)
    {
        loadMenu();
    }
}
