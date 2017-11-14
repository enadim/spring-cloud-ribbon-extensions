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

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;

/**
 * Filters Servers that does have the desired metadata entry.
 * <p>The metadata key is fetched from the context entry with the desired key {@link #dynamicEntryKey}
 * <p>Concrete use case with a µservice that share a point to point connection with external system (this connection is established only once): market access / FIX connection.
 * This µservice should add the connection up marker to its metadata so that the clients can target the instance holding the connection.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class DynamicMetadataMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the dynamic entry key. used to get the metadata key to match.
     */
    private final String dynamicEntryKey;

    /**
     * Sole constructor.
     *
     * @param dynamicEntryKey the dynamic metadata key.
     */
    public DynamicMetadataMatcher(String dynamicEntryKey) {
        this.dynamicEntryKey = dynamicEntryKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String metadataKey = current().get(dynamicEntryKey);
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        if (metadataKey != null) {
            String expected = current().get(metadataKey);
            String actual = metadata.get(metadataKey);
            boolean accept = (expected == null && actual == null) || (expected != null && expected.equals(actual));
            log.trace("Expected [{}={}] vs {}{} => {}",
                    metadataKey,
                    expected,
                    server.getHostPort(),
                    metadata,
                    accept);
            return accept;
        } else {
            log.trace("[{}] not defined! : {}{} => false",
                    dynamicEntryKey,
                    server.getHostPort(),
                    metadata);
            return false;
        }
    }
}
