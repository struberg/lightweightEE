/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.jaxenter.eesummit.caroline.backend.test;

import de.jaxenter.eesummit.caroline.backend.test.CdiContainerTest;
import org.testng.annotations.Test;

/**
 *  This is an empty test class for profiling the OpenWebBeans
 *  boot performance with YourKit.
 *
 *  I confess to 'abuse' my toy project to do some OWB tuning ;)
 */
public class EmptyTest extends CdiContainerTest
{

    @Test
    public void testNothin() {
        // wohu, this line should be really quick ;)
    }
}
