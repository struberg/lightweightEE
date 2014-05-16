package de.jaxenter.eesummit.caroline.backend.test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;


import java.util.Properties;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.config.PropertyLoader;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;

import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CdiContainerTest.class);

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
            ProjectStage projectStage = runInProjectStage();
            ProjectStageProducer.setProjectStage(projectStage);

            String dbvendor = ConfigResolver.getPropertyValue("dbvendor", "mysql");

            logger.info("Starting OpenEJB with ProjectStage={} and dbvendor={}", projectStage, dbvendor);


            cdiContainer = CdiContainerLoader.getCdiContainer();

            Properties bootProperties = PropertyLoader.getProperties("db/db-" + dbvendor + ".properties");
            cdiContainer.boot(bootProperties);

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
