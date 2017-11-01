/**
 * Copyright (c) 2017 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.predicate;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

/**
 * Filters Servers against a specific {@link #attributeName} that matches {@link #expectedValue}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class StaticMatcher extends DiscoveryEnabledServerPredicate {
    private final String attributeName;
    private final String expectedValue;

    public StaticMatcher(final @NotNull String attributeName, final @NotNull String expectedValue) {
        this.attributeName = attributeName;
        this.expectedValue = expectedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String  actual = server.getInstanceInfo().getMetadata().get(attributeName);
        boolean accept = expectedValue.equals(actual);
        log.trace("expected=[{}] to {}[{}={}] => {}",
                  expectedValue,
                  server.getHostPort(),
                  attributeName,
                  actual,
                  accept);
        return accept;
    }
}
