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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;

/**
 * A single tyre of a car.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@Entity
public class Tyre implements BaseEntity
{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Integer optlock;


    @Column(length=20)
    private String vendor;

    /** the width in inches */
    private int treadWidth;

    /** in % */
    private int profileHeigth;

    /** in inches */
    private int wheelDiameter;

    /**
     * max allowed speed
     *   mpH  kmH
     * N  87  140
     * P  93  150
     * Q  99  160
     * R 106  170
     * S 112  180
     * T 118  190
     * U 124  200
     * H 130  210
     * V 149  240
     * Z 150+ 240+
     * W 168  270
     * Y 186  300
     */
    public enum SpeedRating
    {
        N,
        P,
        Q,
        R,
        S,
        T,
        U,
        H,
        V,
        Z,
        W,
        Y
    }

    @Enumerated(EnumType.STRING)
    private SpeedRating speedRating;

    public enum Usage
    {
        WINTER,
        SUMMER,
        ALLSEASON
    }

    @Enumerated(EnumType.STRING)
    private Usage usage;

    @Min(1980)
    @Past
    private int prodYear;

    @Min(1)
    @Max(12)
    private int prodMonth;


    public Long getId()
    {
        return id;
    }

    public Integer getOptlock()
    {
        return optlock;
    }

    public int getProdMonth()
    {
        return prodMonth;
    }

    public void setProdMonth(int prodMonth)
    {
        this.prodMonth = prodMonth;
    }

    public int getProdYear()
    {
        return prodYear;
    }

    public void setProdYear(int prodYear)
    {
        this.prodYear = prodYear;
    }

    public int getProfileHeigth()
    {
        return profileHeigth;
    }

    public void setProfileHeigth(int profileHeigth)
    {
        this.profileHeigth = profileHeigth;
    }

    public SpeedRating getSpeedRating()
    {
        return speedRating;
    }

    public void setSpeedRating(SpeedRating speedRating)
    {
        this.speedRating = speedRating;
    }

    public int getTreadWidth()
    {
        return treadWidth;
    }

    public void setTreadWidth(int treadWidth)
    {
        this.treadWidth = treadWidth;
    }

    public Usage getUsage()
    {
        return usage;
    }

    public void setUsage(Usage usage)
    {
        this.usage = usage;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    public int getWheelDiameter()
    {
        return wheelDiameter;
    }

    public void setWheelDiameter(int wheelDiameter)
    {
        this.wheelDiameter = wheelDiameter;
    }
}
