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

package org.apache.servicecomb.faultinjection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.servicecomb.core.Handler;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.swagger.invocation.AsyncResponse;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

/**
 * Fault injection handler which injects the delay/abort for requests based on
 * the percentage configured in service file.
 *
 */

public class FaultInjectionHandler implements Handler {

  private static List<Fault> faultInjectionFeatureList = new ArrayList<>();

  //added only for unit testing
  public void setFaultFeature(List<Fault> faultFeature) {
    faultInjectionFeatureList = faultFeature;
  }

  public static List<Fault> getFaultFeature() {
    return faultInjectionFeatureList;
  }

  @Override
  public void handle(Invocation invocation, AsyncResponse asyncResp) throws Exception {

    // prepare the key and lookup for request count.
    String key = invocation.getTransport().getName() + invocation.getMicroserviceQualifiedName();
    AtomicLong reqCount = FaultInjectionUtil.getOperMetTotalReq(key);
    long reqCountCurrent = reqCount.get();

    // increment the request count here after checking the delay/abort condition.
    reqCount.incrementAndGet();

    FaultParam param = new FaultParam(reqCountCurrent);
    Context currentContext = Vertx.currentContext();
    if (currentContext != null && currentContext.isEventLoopContext()) {
      param.setVertx(currentContext.owner());
    }

    FaultExecutor executor = new FaultExecutor(faultInjectionFeatureList, invocation, param);
    executor.execute(response -> {
      try {
        if (response.isFailed()) {
          asyncResp.complete(response);
        } else {
          FaultResponse r = response.getResult();
          if (r.getStatusCode() == FaultInjectionConst.FAULT_INJECTION_ERROR) {
            asyncResp.consumerFail(new InvocationException(r.getErrorCode(), "", r.getErrorData()));
            return;
          }
          invocation.next(asyncResp);
        }
      } catch (Exception e) {
        asyncResp.consumerFail(e);
      }
    });
  }
}
