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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

/**
 * tyre change event.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Entity
public class TyreChange implements BaseEntity
{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Integer optlock;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    private boolean workDone;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Employee reservedBy;

    @ManyToOne
    private Employee executedBy;


    public Long getId()
    {
        return id;
    }

    public Integer getOptlock()
    {
        return optlock;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public boolean isWorkDone()
    {
        return workDone;
    }

    public void setWorkDone(boolean workDone)
    {
        this.workDone = workDone;
    }
}
