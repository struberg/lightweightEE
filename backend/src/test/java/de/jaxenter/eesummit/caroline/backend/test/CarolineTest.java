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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Base class for our backend tests.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
public class CarolineTest extends Arquillian
{

    /**
     * This should not be necessary in the future!
     * I will contribute a ClassPathArchive to Arquillian which will
     * provide this out of the box!
     */
    public static final String BEANS_XML =
        "<beans>\n" +
        "    <interceptors>\n" +
        "        <class>org.apache.myfaces.extensions.cdi.jpa.impl.transaction.TransactionalInterceptor</class>\n" +
        "    </interceptors>\n" +
        "</beans>";

    /**
     * This should not be necessary in the future!
     * I will contribute a ClassPathArchive to Arquillian which will
     * provide this out of the box!
     */
    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackages(true, "de.jaxenter.eesummit.caroline.backend",
                                   "org.apache.myfaces.extensions.cdi")
                .addAsManifestResource(new ByteArrayAsset(BEANS_XML.getBytes()),
                        ArchivePaths.create("beans.xml"));
    }

}
