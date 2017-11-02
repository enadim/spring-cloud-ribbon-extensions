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
import java.util.Map;

/**
 * Filters Servers against a specific {@link #attributeKey} that matches the {@link #expectedValue}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class StaticMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the attribute key to match.
     */
    private final String attributeKey;
    /**
     * the expected value
     */
    private final String expectedValue;

    /**
     * Sole constructor
     *
     * @param attributeKey  the attribute key to match.
     * @param expectedValue the expected value
     */
    public StaticMatcher(@NotNull String attributeKey, @NotNull String expectedValue) {
        this.attributeKey = attributeKey;
        this.expectedValue = expectedValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        String actual = metadata.get(attributeKey);
        boolean accept = expectedValue.equals(actual);
        log.trace("Expected [{}] vs {}{} => {}",
                expectedValue,
                server.getHostPort(),
                metadata,
                accept);
        return accept;
    }
}
