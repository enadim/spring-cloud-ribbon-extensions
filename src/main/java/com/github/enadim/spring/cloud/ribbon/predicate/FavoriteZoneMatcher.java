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

import com.github.enadim.spring.cloud.ribbon.support.FavoriteZoneConfig;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;

/**
 * Filters Servers against the favorite zone.
 *
 * @author Nadim Benabdenbi
 * @see FavoriteZoneConfig for a concrete usage
 */
@Slf4j
public class FavoriteZoneMatcher extends NullSafeServerPredicate {
    /**
     * the favorite zone entry key.
     */
    private final String entryKey;

    /**
     * Sole Constructor.
     *
     * @param entryKey the favorite zone entry key.
     */
    public FavoriteZoneMatcher(@NotNull String entryKey) {
        this.entryKey = entryKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(PredicateKey input) {
        Server server = input.getServer();
        String expected = current().get(entryKey);
        String actual = server.getZone();
        boolean accept = expected != null && expected.equals(actual);
        log.trace("Expected [{}] vs {}[{}] => {}",
                expected,
                server.getHostPort(),
                actual,
                accept);
        return accept;
    }
}
