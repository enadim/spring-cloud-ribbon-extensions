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

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.lang.String.format;

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
     * matches result when dynamic entry key is missing.
     */
    private final boolean matchIfMissing;

    /**
     * Sole constructor.
     *
     * @param dynamicEntryKey the dynamic metadata key.
     * @param matchIfMissing  the result when dynamic entry key is not defined
     */
    public DynamicMetadataMatcher(@NotNull String dynamicEntryKey, boolean matchIfMissing) {
        this.dynamicEntryKey = dynamicEntryKey;
        this.matchIfMissing = matchIfMissing;
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
            log.trace("Expected [{}={}] vs {}:{}{} => {}",
                    metadataKey,
                    expected,
                    server.getHostPort(),
                    server.getMetaInfo().getAppName(),
                    metadata,
                    accept);
            return accept;
        } else {
            log.trace("[{}] not defined! : {}{} => %b",
                    dynamicEntryKey,
                    server.getHostPort(),
                    metadata,
                    matchIfMissing);
            return matchIfMissing;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String metadataKey = current().get(dynamicEntryKey);
        return format("DynamicMetadataMatcher[(%s=%s)=%s,matchIfMissing=%b]", dynamicEntryKey, metadataKey, metadataKey == null ? null : current().get(metadataKey), matchIfMissing);
    }
}
