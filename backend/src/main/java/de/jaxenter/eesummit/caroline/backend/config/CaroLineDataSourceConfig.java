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
package de.jaxenter.eesummit.caroline.backend.config;

import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.jpa.api.datasource.DataSourceConfig;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import java.util.Properties;

/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@Dependent
public class CaroLineDataSourceConfig implements DataSourceConfig
{
    private @Inject
    ProjectStage projectStage;

    @Override
    public String getJndiResourceName(String connectionId)
    {
        return null;
    }

    @Override
    public String getConnectionClassName(String connectionId)
    {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public Properties getConnectionProperties(String connectionId)
    {
        Properties props = new Properties();

        props.put("user", "root");
        return props;
    }

    @Override
    public String getJdbcConnectionUrl(String connectionId)
    {
        return "jdbc:mysql://localhost/caroline?autoReconnect=true";
    }
}
