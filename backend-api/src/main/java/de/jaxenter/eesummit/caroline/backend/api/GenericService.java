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
package de.jaxenter.eesummit.caroline.backend.api;

import de.jaxenter.eesummit.caroline.entities.BaseEntity;


/**
 * Generic Service Facade for basic CRUD operations.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 * @param <T> Entity class that this service manages
 */
public interface GenericService<T extends BaseEntity>
{

    /**
     * Updates entity. Merges the state of the given object into the persistence context.
     *
     * @param entity Entity to merge
     * @return The instance that was updated
     */
    T update(T entity);

    /**
     * Saves entity in database. Calls update() or create(), depending on entity state.
     *
     * @param entity Entity
     * @return Saved entity
     */
    T save(T entity);

    /**
     * Get by primary key.
     *
     * @param id Primary key
     * @return Found entity or null, if entity does not exist
     */
    T getById(Object id);

    /**
     * Reattaches given entity to persistence context. Basically uses #getById() if entity is already persisted.
     *
     * @param entity entity to reattach
     * @return reattached entity or input entity, if entity is not persisted
     */
    T reattach(T entity);

    /**
     * Checks if entity is managed. Managed means, that the entity is persisted in the db (but could be deattached)
     *
     * @param entity entity to check
     * @return true, if entity is managed entity, false otherwise.
     */
    boolean isManaged(T entity);
}
