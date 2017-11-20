/*
 *  Copyright 2017 Huawei Technologies Co., Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.servicecomb.samples.bmi;

import org.springframework.stereotype.Service;

import io.servicecomb.serviceregistry.RegistryUtils;
import io.servicecomb.serviceregistry.api.registry.MicroserviceInstance;

@Service
public class InstanceInfoServiceImpl implements InstanceInfoService {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getInstanceId() {

    MicroserviceInstance instance = RegistryUtils.getMicroserviceInstance();
    if (instance == null) {
      throw new IllegalStateException(
          "unable to find any service instances, maybe there is problem registering in service center?");
    }
    return instance.getInstanceId();
  }
  
}
