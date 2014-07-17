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
package de.jaxenter.eesummit.caroline.backend.test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;


import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;

import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;


/**
 * A base class which shows how to simply implement a CDI unit test
 * utilizing the Apache DeltaSpike CdiControl.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public abstract class CdiContainerTest {

    protected static CdiContainer cdiContainer;

    // nice to know, since testng executes tests in parallel.
    protected static int containerRefCount = 0;

    protected ProjectStage runInProjectStage() {
        return ProjectStage.UnitTest;
    }

    /**
     * Starts container
     * @throws Exception in case of severe problem
     */
    @BeforeMethod
    public final void setUp() throws Exception {
        containerRefCount++;

        if (cdiContainer == null) {
            ProjectStageProducer.setProjectStage(runInProjectStage());

            cdiContainer = CdiContainerLoader.getCdiContainer();
            cdiContainer.boot();
            cdiContainer.getContextControl().startContexts();
        }
        else {
            cleanInstances();
        }
    }

    @BeforeClass
    public final void beforeClass() throws Exception {
        setUp();
        cdiContainer.getContextControl().stopContext(RequestScoped.class);
        cdiContainer.getContextControl().startContext(RequestScoped.class);

        // perform injection into the very own test class
        BeanManager beanManager = cdiContainer.getBeanManager();

        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(this, creationalContext);
    }

    /**
     * Shuts down container.
     * @throws Exception in case of severe problem
     */
    @AfterMethod
    public final void tearDown() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.getContextControl().stopContext(RequestScoped.class);
            cdiContainer.getContextControl().startContext(RequestScoped.class);
            containerRefCount--;
        }
    }

    public final void cleanInstances() throws Exception {
        cdiContainer.getContextControl().stopContexts();
        cdiContainer.getContextControl().startContexts();
    }

    @AfterSuite
    public synchronized void shutdownContainer() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.shutdown();
            cdiContainer = null;
        }
    }

    public void finalize() throws Throwable {
        shutdownContainer();
        super.finalize();
    }


    /**
     * Override this method for database clean up.
     *
     * @throws Exception in case of severe problem
     */
    protected void cleanUpDb() throws Exception {
        //Override in subclasses when needed
    }


}
