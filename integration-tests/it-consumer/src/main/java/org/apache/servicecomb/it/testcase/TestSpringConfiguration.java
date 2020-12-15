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
package org.apache.servicecomb.it.testcase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.junit.Test;
import org.springframework.core.env.Environment;

public class TestSpringConfiguration {
  @Test
  public void should_resolve_placeholder_in_list_by_spring_environment() {
    Environment environment = BeanUtils.getContext().getEnvironment();

    String value = environment.getProperty("placeholder");
    assertThat(value).isEqualTo("actual-value1,actual-value2");

    @SuppressWarnings("unchecked")
    List<String> list = environment.getProperty("placeholder", List.class);
    assertThat(list).containsExactly("actual-value1", "actual-value2");
  }
}
