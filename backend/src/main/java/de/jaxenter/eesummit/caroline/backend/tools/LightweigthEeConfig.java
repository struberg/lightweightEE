/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jaxenter.eesummit.caroline.backend.tools;

import org.apache.deltaspike.core.api.config.PropertyFileConfig;

/**
 * Configuration for our application.
 * All property files with name 'lightweightee.properties' will get picked up
 * as {@link org.apache.deltaspike.core.spi.config.ConfigSource}s.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class LightweigthEeConfig implements PropertyFileConfig
{
    @Override
    public String getPropertyFileName()
    {
        return "lightweightee.properties";
    }

    public boolean isOptional()
    {
        return true;
    }
}
