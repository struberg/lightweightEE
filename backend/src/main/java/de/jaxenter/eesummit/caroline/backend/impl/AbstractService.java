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
package de.jaxenter.eesummit.caroline.backend.impl;

import de.jaxenter.eesummit.caroline.backend.api.GenericService;
import de.jaxenter.eesummit.caroline.entities.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Abstract implementation for basic CRUD services.
 * All services which implement the CRUD operations should extend this class.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 * @param <T> Entity class that this service manages
 */
public abstract class AbstractService<T extends BaseEntity> implements GenericService<T>
{

    // Cache for getEntityClass()
    private Class<T> entityClass;

    /**
     * Clear the EntityManager.
     *
     * ATTENTION! This must only be used to temporarily work around OpenJPA bugs!
     * That's the reason why it is deprecated!
     */
    @Deprecated
    public void clearEm()
    {
        getEm().clear();
    }

    /**
     * Initially store the entity into the database.
     * For 'logical' deletes, an own 'active' flag should get used instead.
     *
     * @return the freshly persisted entity
     */
    protected T create(final T entity)
    {
        getEm().persist(entity);
        return entity;
    }


    /**
     * Physically delete the entity from the DB.
     *
     * @param entity
     */
    protected void delete(final T entity)
    {
        if (entity == null) { return; }
        T dbentity = getEm().getReference(getEntityClass(), entity.getId());
        getEm().remove(dbentity);
    }

    @Override
    public T getById(final Object primaryKey)
    {
        return getEm().find(getEntityClass(), primaryKey);
    }

    @Override
    public T update(final T entity)
    {
        return getEm().merge(entity);
    }

    @Override
    public T save(final T entity)
    {
        if (isManaged(entity))
        {
            return update(entity);
        }
        else
        {
            return create(entity);
        }
    }

    @Override
    public T reattach(T entity)
    {
        if (isManaged(entity))
        {
            return update(entity);
        }
        return entity;
    }

    @Override
    public boolean isManaged(T entity)
    {
        return entity != null && entity.getId() != null;
    }

    /**
     * Returns query for all entities of given type. Overwrite if necessary.
     *
     * @return Query for all entities
     */
    protected Query getListQuery()
    {
        Class<?> entityClass = getEntityClass();
        return getEm().createQuery("SELECT entity FROM " + entityClass.getSimpleName() + " entity", entityClass);
    }

    /**
     * Returns entity class for this service. Uses reflection, overwrite if necessary.
     *
     * @return Entity class
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass()
    {
        if (entityClass == null)
        {
            final Type thisType = getClass().getGenericSuperclass();
            final Type entityType;
            if (thisType instanceof ParameterizedType)
            {
                entityType = ((ParameterizedType) thisType).getActualTypeArguments()[0];
            }
            else if (thisType instanceof Class)
            {
                entityType = ((ParameterizedType) ((Class) thisType).getGenericSuperclass()).getActualTypeArguments()[0];
            }
            else
            {
                throw new IllegalArgumentException("Problem handling type construction for " + getClass());
            }

            if (entityType instanceof Class)
            {
                entityClass = (Class<T>) entityType;
            }
            else if (entityType instanceof ParameterizedType)
            {
                entityClass = (Class<T>) ((ParameterizedType) entityType).getRawType();
            }
            else
            {
                throw new IllegalArgumentException("Problem determining the class of the generic for " + getClass());
            }
        }

        return entityClass;
    }

    /**
     * Get entity manager.
     *
     * @return Entity manager
     */
    abstract protected EntityManager getEm();
}
