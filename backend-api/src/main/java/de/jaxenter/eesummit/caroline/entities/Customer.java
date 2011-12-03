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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * A Customer is a user who buys something ...
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Entity
@DiscriminatorValue("C")
@Table
public class Customer extends CaroLineUser
{

    private String email;

    @Column(length=20)
    private String carVendor;

    @Column(length=20)
    private String carType;

    @OneToOne(mappedBy = "customer", optional = true)
    private StorageSlot storageSlot;

    @OneToMany
    private List<Tyre> mountedTyres;



    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCarType()
    {
        return carType;
    }

    public void setCarType(String carType)
    {
        this.carType = carType;
    }

    public String getCarVendor()
    {
        return carVendor;
    }

    public void setCarVendor(String carVendor)
    {
        this.carVendor = carVendor;
    }

    public StorageSlot getStorageSlot()
    {
        return storageSlot;
    }

    public void setStorageSlot(StorageSlot storageSlot)
    {
        this.storageSlot = storageSlot;
    }

    public List<Tyre> getMountedTyres()
    {
        return mountedTyres;
    }

    public void setMountedTyres(List<Tyre> mountedTyres)
    {
        this.mountedTyres = mountedTyres;
    }
}
