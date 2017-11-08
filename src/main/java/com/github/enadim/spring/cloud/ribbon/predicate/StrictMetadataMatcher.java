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
import com.github.enadim.spring.cloud.ribbon.support.StrictMetadataMatcherConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Strict server metadata matcher over all the execution context entries.
 *
 * @author Nadim Benabdenbi
 * @see StrictMetadataMatcherConfig for a concrete usage
 */
@Slf4j
public class StrictMetadataMatcher extends DiscoveryEnabledServerPredicate {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        ExecutionContext context = ExecutionContextHolder.current();
        Set<Entry<String, String>> expected = context.entrySet();
        Map<String, String> actual = server.getInstanceInfo().getMetadata();
        boolean accept = actual.entrySet().containsAll(expected);
        log.trace("Expected {} vs {}{} => {}",
                expected,
                server.getHostPort(),
                actual,
                accept);
        return accept;
    }
}
