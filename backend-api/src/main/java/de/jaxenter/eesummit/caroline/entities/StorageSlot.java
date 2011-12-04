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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.util.List;

/**
 * The place where 4 tyres get stored
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Entity
public class StorageSlot implements BaseEntity
{

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Integer optlock;

    @ManyToOne(optional=false)
    private Warehouse warehouse;

    @Column(length=10, nullable = false)
    private String slotNumber;

    @OneToOne(optional = true)
    @JoinColumn
    private Customer customer;

    @OneToMany
    private List<Tyre> storedTyres;


    public Long getId()
    {
        return id;
    }

    public Integer getOptlock()
    {
        return optlock;
    }

    public Warehouse getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse)
    {
        this.warehouse = warehouse;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public List<Tyre> getStoredTyres()
    {
        return storedTyres;
    }

    public void setStoredTyres(List<Tyre> storedTyres)
    {
        this.storedTyres = storedTyres;
    }

    public String getSlotNumber()
    {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber)
    {
        this.slotNumber = slotNumber;
    }
}
