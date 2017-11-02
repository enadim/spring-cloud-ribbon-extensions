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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Filters Servers metadata attribute against a specific {@link #attributeKey} defined in {@link RibbonRuleContext}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class AttributeMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the attribute key to test against
     */
    private final String attributeKey;

    /**
     * Sole Constructor.
     *
     * @param attributeKey the attribute key.
     */
    public AttributeMatcher(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String expected = RibbonRuleContextHolder.current().get(attributeKey);
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        String actual = metadata.get(attributeKey);
        boolean accept = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        log.trace("Expected [{}] vs {}{} => {}",
                attributeKey,
                expected,
                server.getHostPort(),
                metadata,
                accept);
        return accept;
    }
}
