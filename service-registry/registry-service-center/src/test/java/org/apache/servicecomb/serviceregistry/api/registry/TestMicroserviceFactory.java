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

package org.apache.servicecomb.serviceregistry.api.registry;

import static org.apache.servicecomb.registry.definition.DefinitionConst.CONFIG_ALLOW_CROSS_APP_KEY;

import org.apache.commons.configuration.Configuration;
import org.apache.servicecomb.config.BootStrapProperties;
import org.apache.servicecomb.config.ConfigUtil;
import org.apache.servicecomb.registry.api.registry.Microservice;
import org.apache.servicecomb.registry.api.registry.MicroserviceFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestMicroserviceFactory {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testAllowCrossApp() {
    Microservice microservice = new Microservice();
    Assert.assertFalse(microservice.allowCrossApp());

    microservice.getProperties().put(CONFIG_ALLOW_CROSS_APP_KEY, "true");
    Assert.assertTrue(microservice.allowCrossApp());

    microservice.getProperties().put(CONFIG_ALLOW_CROSS_APP_KEY, "false");
    Assert.assertFalse(microservice.allowCrossApp());

    microservice.getProperties().put(CONFIG_ALLOW_CROSS_APP_KEY, "asfas");
    Assert.assertFalse(microservice.allowCrossApp());
  }

  @Test
  public void testInit() {
    MicroserviceFactory factory = new MicroserviceFactory();
    Microservice microservice = factory.create(ConfigUtil.createLocalConfig());

    String microserviceName = "default";

    Assert.assertEquals(microserviceName, microservice.getServiceName());
  }

  @Test
  public void testSetDescription() {
    MicroserviceFactory factory = new MicroserviceFactory();
    Configuration configuration = ConfigUtil.createLocalConfig();
    configuration.setProperty(BootStrapProperties.CONFIG_SERVICE_DESCRIPTION, new String[] {"test1", "test2"});
    Microservice microservice = factory.create(configuration);
    Assert.assertEquals("test1,test2", microservice.getDescription());
  }

  @Test
  public void testSetDescriptionOnNullDescription() {
    Configuration configuration = ConfigUtil.createLocalConfig();
    configuration.clearProperty(BootStrapProperties.CONFIG_SERVICE_DESCRIPTION);

    MicroserviceFactory factory = new MicroserviceFactory();
    Microservice microservice = factory.create(configuration);
    Assert.assertNull(microservice.getDescription());

    configuration.setProperty(BootStrapProperties.CONFIG_SERVICE_DESCRIPTION, new String[] {});
    microservice = factory.create(configuration);

    Assert.assertNull(microservice.getDescription());
  }

  @Test
  public void testSetDescriptionOnEmptyDescription() {
    Configuration configuration = ConfigUtil.createLocalConfig();
    configuration.setProperty(BootStrapProperties.CONFIG_SERVICE_DESCRIPTION, new String[] {"", ""});

    MicroserviceFactory factory = new MicroserviceFactory();

    Microservice microservice = factory.create(configuration);

    Assert.assertEquals(",", microservice.getDescription());
  }

  @Test
  public void testSetDescriptionOnBlankDescription() {
    Configuration configuration = ConfigUtil.createLocalConfig();
    configuration.setProperty(BootStrapProperties.CONFIG_SERVICE_DESCRIPTION, new String[] {" ", " "});

    MicroserviceFactory factory = new MicroserviceFactory();

    Microservice microservice = factory.create(configuration);

    Assert.assertEquals(" , ", microservice.getDescription());
  }

  @Test
  public void testCreateMicroserviceFromDefinitionWithInvalidVersion() {
    Configuration configuration = ConfigUtil.createLocalConfig();
    configuration.setProperty(BootStrapProperties.CONFIG_SERVICE_VERSION, "x.y.x.1");

    expectedException.equals(IllegalStateException.class);
    expectedException.expectMessage("Invalid major \"x\", version \"x.y.x.1\".");
    MicroserviceFactory microserviceFactory = new MicroserviceFactory();
    microserviceFactory.create(configuration);
  }
}
