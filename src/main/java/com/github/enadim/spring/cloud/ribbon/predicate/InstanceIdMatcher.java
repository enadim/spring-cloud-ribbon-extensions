/*
 * Copyright (c) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.predicate;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import static java.lang.String.format;

/**
 * Filters servers against the {@link #expectedInstanceId}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class InstanceIdMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * The expected instance id to match.
     */
    private final String expectedInstanceId;

    /**
     * Sole constructor.
     *
     * @param expectedInstanceId The expected instance id to match.
     */
    public InstanceIdMatcher(@NotNull String expectedInstanceId) {
        this.expectedInstanceId = expectedInstanceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String actual = server.getInstanceInfo().getInstanceId();
        boolean accept = expectedInstanceId.equals(actual);
        log.trace("Expected [{}] vs {}:{}[{}] => {}",
                expectedInstanceId,
                server.getHostPort(),
                server.getMetaInfo().getAppName(),
                actual,
                accept);
        return accept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return format("InstanceIdMatcher[instanceId=%s]", expectedInstanceId);
    }
}
