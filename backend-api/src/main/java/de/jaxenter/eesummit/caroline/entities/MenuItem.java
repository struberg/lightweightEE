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
package de.jaxenter.eesummit.caroline.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.util.Arrays;
import java.util.List;

/**
 * A single line in the menu
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@Entity
public class MenuItem implements BaseEntity
{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Integer optlock;

    private int level;

    /** for sorting the menu */
    private int ordinal;

    @ManyToOne
    private MenuItem parent;

    private transient MenuItem[] children;

    @Column(length = 20)
    private String resourceKey;

    private String action;

    /** space separated list or roles */
    private String roles;

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public Integer getOptlock()
    {
        return optlock;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public MenuItem[] getChildren()
    {
        return children;
    }

    public void setChildren(MenuItem[] children)
    {
        this.children = children;
    }

    public MenuItem getParent()
    {
        return parent;
    }

    public void setParent(MenuItem parent)
    {
        this.parent = parent;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getResourceKey()
    {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey)
    {
        this.resourceKey = resourceKey;
    }

    public String getRoles()
    {
        return roles;
    }

    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    public List<String> getRoleList()
    {
        if (roles == null)
        {
            return null;
        }

        return Arrays.asList(roles.split(" "));
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(int ordinal)
    {
        this.ordinal = ordinal;
    }
}
