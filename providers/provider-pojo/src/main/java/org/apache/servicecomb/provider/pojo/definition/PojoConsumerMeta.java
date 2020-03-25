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
package org.apache.servicecomb.provider.pojo.definition;

import java.util.HashMap;
import java.util.Map;

import org.apache.servicecomb.core.definition.MicroserviceMeta;
import org.apache.servicecomb.core.definition.OperationMeta;
import org.apache.servicecomb.core.definition.SchemaMeta;
import org.apache.servicecomb.core.provider.consumer.MicroserviceReferenceConfig;
import org.apache.servicecomb.swagger.engine.SwaggerConsumer;
import org.apache.servicecomb.swagger.engine.SwaggerConsumerOperation;
import org.apache.servicecomb.swagger.generator.OperationGenerator;
import org.apache.servicecomb.swagger.generator.SwaggerGenerator;

public class PojoConsumerMeta {
  private MicroserviceReferenceConfig microserviceReferenceConfig;

  private SchemaMeta schemaMeta;

  // key is consumer method name
  private Map<PojoConsumerMapKey, PojoConsumerOperationMeta> operationMetas = new HashMap<>();

  public PojoConsumerMeta(MicroserviceReferenceConfig microserviceReferenceConfig, SwaggerConsumer swaggerConsumer,
      SchemaMeta schemaMeta) {
    this.microserviceReferenceConfig = microserviceReferenceConfig;
    this.schemaMeta = schemaMeta;

    SwaggerGenerator intfSwaggerGenerator = SwaggerGenerator.create(swaggerConsumer.getConsumerIntf());
    intfSwaggerGenerator.scanClassAnnotation();
    for (SwaggerConsumerOperation swaggerConsumerOperation : swaggerConsumer.getOperations().values()) {
      String operationId = swaggerConsumerOperation.getSwaggerOperation().getOperationId();
      // SwaggerConsumer has make sure can find operationMeta
      OperationMeta operationMeta = schemaMeta.ensureFindOperation(operationId);

      OperationGenerator intfOperationGenerator = intfSwaggerGenerator
          .createOperationGenerator(swaggerConsumerOperation.getConsumerMethod());
      intfOperationGenerator.generateResponse();
      PojoConsumerOperationMeta pojoConsumerOperationMeta = new PojoConsumerOperationMeta(this, operationMeta,
          swaggerConsumerOperation, intfSwaggerGenerator.getSwagger(), intfOperationGenerator.getOperation());

      operationMetas.put(
          new PojoConsumerMapKey(swaggerConsumerOperation.getSchemaOperationId(), swaggerConsumer.getConsumerIntf()),
          pojoConsumerOperationMeta);
    }
  }

  public MicroserviceReferenceConfig getMicroserviceReferenceConfig() {
    return microserviceReferenceConfig;
  }

  public boolean isExpired() {
    return microserviceReferenceConfig.isExpired();
  }

  public MicroserviceMeta getMicroserviceMeta() {
    return schemaMeta.getMicroserviceMeta();
  }

  public SchemaMeta getSchemaMeta() {
    return schemaMeta;
  }

  public PojoConsumerOperationMeta findOperationMeta(String consumerMethodName, Class<?> consumerInf) {
    return operationMetas.get(new PojoConsumerMapKey(consumerMethodName, consumerInf));
  }
}
