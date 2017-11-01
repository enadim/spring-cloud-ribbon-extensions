/**
 * Copyright (c) 2015 the original author or authors
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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder;
import com.github.enadim.spring.cloud.ribbon.support.StrictMetadataMatcherConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Strict matcher over all the {@link RibbonRuleContext} attributes against the server metadata
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
        final RibbonRuleContext   context  = RibbonRuleContextHolder.current();
        final Map<String, String> expected = context.getAttributes();
        final Map<String, String> actual   = server.getInstanceInfo().getMetadata();
        final boolean             accept   = actual.entrySet().containsAll(expected.entrySet());
        log.trace("Applying strict metadata [{}] to {}[metadata={}] => {}",
                  expected,
                  server.getHostPort(),
                  actual,
                  accept);
        return accept;
    }
}
