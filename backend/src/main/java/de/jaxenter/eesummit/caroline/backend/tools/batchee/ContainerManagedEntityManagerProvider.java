/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jaxenter.eesummit.caroline.backend.tools.batchee;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Properties;

import org.apache.batchee.container.services.persistence.jpa.EntityManagerProvider;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * This EntityManagerProvider is used for storing the batch status into the database.
 * We need it to be a @PersistenceUnit to
 */
public class ContainerManagedEntityManagerProvider implements EntityManagerProvider {

    private Helper helper;

    @Override
    public void init(Properties batchConfig) {
        helper = BeanProvider.getContextualReference(Helper.class);
    }

    @Override
    public EntityManager newEntityManager() {
        return helper.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public void release(EntityManager entityManager) {
        entityManager.close();
    }

    /**
     * Small helper to get our hands onto the PersistenceUnit We need to do that trick as the JNDI location for
     * resource-local connections is different on each and every damn server... With this trick we let the
     * PersistenceUnitInfo of the respective container deal with it.
     */
    @ApplicationScoped
    public static class Helper {

        @PersistenceUnit(unitName = "batchee")
        private EntityManagerFactory entityManagerFactory;

        public EntityManagerFactory getEntityManagerFactory() {
            return entityManagerFactory;
        }
    }
}
