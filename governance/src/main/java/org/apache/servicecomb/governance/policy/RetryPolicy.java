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
package org.apache.servicecomb.governance.policy;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RetryPolicy extends AbstractPolicy {

  public static final int DEFAULT_MAX_ATTEMPTS = 3;

  public static final Duration DEFAULT_WAIT_DURATION = Duration.ofMillis(10);

  public static final String DEFAULT_RETRY_ON_RESPONSE_STATUS = "502";

  private static final Duration INITIAL_INTERVAL = Duration.ofMillis(1000);

  private static final float MULTIPLIER = 2;

  private static final double RANDOMIZATION_FACTOR = 0.5;

  private static final String DEFAULT_RETRY_STRATEGY = "FixedInterval";

  //最多尝试次数
  private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

  //每次重试尝试等待的时间。
  private String waitDuration = DEFAULT_WAIT_DURATION.toString();

  //需要重试的http status, 逗号分隔
  private List<String> retryOnResponseStatus = new ArrayList<>();

  private String retryStrategy = DEFAULT_RETRY_STRATEGY;

  private String initialInterval = INITIAL_INTERVAL.toString();

  private float multiplier = MULTIPLIER;

  private double randomizationFactor = RANDOMIZATION_FACTOR;

  public List<String> getRetryOnResponseStatus() {
    if (CollectionUtils.isEmpty(retryOnResponseStatus)) {
      this.retryOnResponseStatus.add(DEFAULT_RETRY_ON_RESPONSE_STATUS);
    }
    return retryOnResponseStatus;
  }

  public void setRetryOnResponseStatus(List<String> retryOnResponseStatus) {
    this.retryOnResponseStatus = retryOnResponseStatus;
  }

  public int getMaxAttempts() {
    return maxAttempts + 1;
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public String getWaitDuration() {
    return Duration.parse(waitDuration).toMillis() < 10 ? DEFAULT_WAIT_DURATION.toString() : waitDuration;
  }

  public void setWaitDuration(String waitDuration) {
    this.waitDuration = stringOfDuration(waitDuration, DEFAULT_WAIT_DURATION);
  }

  public String getRetryStrategy() {
    if (StringUtils.isEmpty(retryStrategy)) {
      retryStrategy = DEFAULT_RETRY_STRATEGY;
    }
    return retryStrategy;
  }

  public void setRetryStrategy(String retryStrategy) {
    this.retryStrategy = retryStrategy;
  }

  public String getInitialInterval() {
    return initialInterval;
  }

  public void setInitialInterval(String initialInterval) {
    this.initialInterval = stringOfDuration(initialInterval, INITIAL_INTERVAL);
  }

  public float getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

  public double getRandomizationFactor() {
    return randomizationFactor;
  }

  public void setRandomizationFactor(double randomizationFactor) {
    this.randomizationFactor = randomizationFactor;
  }

  @Override
  public boolean isValid() {
    if (maxAttempts < 1) {
      return false;
    }
    if (Duration.parse(waitDuration).toMillis() < 0) {
      return false;
    }
    if (Duration.parse(initialInterval).toMillis() < 10) {
      return false;
    }
    return super.isValid();
  }

  @Override
  public String toString() {
    return "RetryPolicy{" +
        "maxAttempts=" + maxAttempts +
        ", waitDuration=" + waitDuration +
        ", retryOnResponseStatus='" + retryOnResponseStatus + '\'' +
        '}';
  }
}
