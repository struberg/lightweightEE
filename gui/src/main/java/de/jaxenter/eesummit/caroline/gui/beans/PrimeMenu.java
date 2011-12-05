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
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * This model creates the data for a PrimeFaces backed menu
 *
 * See also {@link Menu} for a native implementation.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@SessionScoped
@Named
public class PrimeMenu implements Serializable
{
    private String activeMenuItem;

    private MenuModel model;

    /** whether the menu should get rendered */
    private boolean showMenu = false;

    private @Inject MenuService menuSvc;
    private @Inject
    UserController usr;
    private @Inject @Jsf MessageContext messageContext;


    @PostConstruct
    public void init()
    {
        loadMenu();
    }

    private void loadMenu()
    {
        MenuItem topLevelMenu = menuSvc.getMenu(usr.getUser());
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String servletPath = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName()
                      + ":" + externalContext.getRequestServerPort() + externalContext.getRequestContextPath();

        model = new DefaultMenuModel();

        addMenus(model, null, topLevelMenu.getChildren(), servletPath);
    }

    private void addMenus(MenuModel model, Submenu parentMenu, MenuItem[] children, String servletPath)
    {
        if (children == null)
        {
            showMenu = false;
            return;
        }

        showMenu = true;

        for (MenuItem dbMenu : children)
        {
            if (dbMenu.getChildren() != null && dbMenu.getChildren().length > 0)
            {
                Submenu submenu = new Submenu();
                submenu.setId(dbMenu.getResourceKey()+"_Node");
                submenu.setLabel(getText(dbMenu));

                //since the PF Submenu doesn't know links, we have to insert an artificial MenuItem
                org.primefaces.component.menuitem.MenuItem pfMenuItem = new org.primefaces.component.menuitem.MenuItem();
                pfMenuItem.setId(dbMenu.getResourceKey());
                pfMenuItem.setValue(getText(dbMenu));
                pfMenuItem.setUrl(getAction(dbMenu, servletPath));

                submenu.getChildren().add(pfMenuItem);

                addMenus(model, submenu, dbMenu.getChildren(), servletPath);

                if (parentMenu != null)
                {
                    parentMenu.getChildren().add(submenu);
                }
                else
                {
                    model.addSubmenu(submenu);
                }
            }
            else
            {
                org.primefaces.component.menuitem.MenuItem pfMenuItem = new org.primefaces.component.menuitem.MenuItem();
                pfMenuItem.setId(dbMenu.getResourceKey());
                pfMenuItem.setValue(getText(dbMenu));
                pfMenuItem.setUrl(getAction(dbMenu, servletPath));

                if (parentMenu != null)
                {
                    parentMenu.getChildren().add(pfMenuItem);
                }
                else
                {
                    model.addMenuItem(pfMenuItem);
                }

            }
        }
    }

    public String getActiveMenuItem() {
        return activeMenuItem;
    }

    public void activateMenuItem(String activeMenuItem) {
        this.activeMenuItem = activeMenuItem;
    }

    private String getText(MenuItem menuItem)
    {
        return messageContext.message().text("{" + menuItem.getResourceKey() + "}").toText();
    }

    private String getAction(MenuItem menuItem, String servletPath)
    {
        String action = menuItem.getAction();

        if (action != null && (action.startsWith("http://") || action.startsWith("https://")))
        {
            // already a fully qualified URL
            return action;
        }
        return servletPath + action;
    }

    public MenuModel getModel()
    {
        return model;
    }

    /**
     * If the user logs in/out etc, then we need to reload the menu
     */
    protected void onUserSettingsChanged(@Observes UserSettingsChangedEvent usc)
    {
        loadMenu();
    }

    public boolean isShowMenu()
    {
        return showMenu;
    }
}
