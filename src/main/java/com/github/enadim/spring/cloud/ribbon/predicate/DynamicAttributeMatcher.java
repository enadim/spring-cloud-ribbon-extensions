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

import java.util.Map;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;

/**
 * Filters Servers that does not have the desired attribute key with the desired value.
 * <p>The attribute key is fetched from the context attribute having the key {@link #dynamicAttributeKey}
 * <p>Concrete use case with a micro-service that share a point to point connection with external system (this connection is established only once): market access / FIX connection.
 * The micro-service will add the connection marker to its metadata
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class DynamicAttributeMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the dynamic attribute key. used to get the attribute key to match.
     */
    private final String dynamicAttributeKey;

    /**
     * Sole constructor.
     *
     * @param dynamicAttributeKey the dynamic attribute key.
     */
    public DynamicAttributeMatcher(String dynamicAttributeKey) {
        this.dynamicAttributeKey = dynamicAttributeKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String attributeKey = current().get(dynamicAttributeKey);
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        if (attributeKey != null) {
            String expected = current().get(attributeKey);
            String actual = metadata.get(attributeKey);
            boolean accept = (expected == null && actual == null) || (expected != null && expected.equals(actual));
            log.trace("Expected [{}={}] vs {}{} => {}",
                    attributeKey,
                    expected,
                    server.getHostPort(),
                    metadata,
                    accept);
            return accept;
        } else {
            log.trace("[{}] not defined! : {}{} => false",
                    dynamicAttributeKey,
                    server.getHostPort(),
                    metadata);
            return false;
        }
    }
}
