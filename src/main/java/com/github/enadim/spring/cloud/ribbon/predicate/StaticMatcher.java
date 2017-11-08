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
 * Filters Servers against a specific {@link #entryKey} that matches the {@link #entryValue}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class StaticMatcher extends DiscoveryEnabledServerPredicate {
    /**
     * the entry key to match.
     */
    private final String entryKey;
    /**
     * the expected entry value
     */
    private final String entryValue;

    /**
     * Sole constructor
     *
     * @param entryKey   the attribute key to match.
     * @param entryValue the expected entry value
     */
    public StaticMatcher(@NotNull String entryKey, @NotNull String entryValue) {
        this.entryKey = entryKey;
        this.entryValue = entryValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        String actual = metadata.get(entryKey);
        boolean accept = entryValue.equals(actual);
        log.trace("Expected [{}={}] vs {}{} => {}",
                entryKey,
                entryValue,
                server.getHostPort(),
                metadata,
                accept);
        return accept;
    }
}
