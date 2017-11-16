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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Filters Servers metadata against a specific {@link #metadataKey} defined in {@link ExecutionContext}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class SingleMetadataMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the metadata key to test against
     */
    private final String metadataKey;

    /**
     * Sole Constructor.
     *
     * @param metadataKey the metadata key.
     */
    public SingleMetadataMatcher(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String expected = ExecutionContextHolder.current().get(metadataKey);
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        String actual = metadata.get(metadataKey);
        boolean accept = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        log.trace("Expected [{}] vs {}:{}{} => {}",
                metadataKey,
                expected,
                server.getHostPort(),
                server.getMetaInfo().getAppName(),
                metadata,
                accept);
        return accept;
    }
}
