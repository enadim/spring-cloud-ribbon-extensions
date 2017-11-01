/**
 * Copyright (c) 2017 the original author or authors
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
import com.github.enadim.spring.cloud.ribbon.support.FavoriteZoneConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters Servers against a specific {@link #attributeName} defined in {@link RibbonRuleContext}.
 * <p>The filter is applied against the server attribute when the {@link RibbonRuleContext}[{@link #attributeName}] is not null or when the {@link #defaultValue} is not null
 *
 * @author Nadim Benabdenbi
 * @see FavoriteZoneConfig for a concrete usage
 */
@Slf4j
public class AttributeMatcher extends DiscoveryEnabledServerPredicate {
    private final String attributeName;
    private final String defaultValue;

    public AttributeMatcher(final String attributeName, final String defaultValue) {
        this.attributeName = attributeName;
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doApply(DiscoveryEnabledServer server) {
        String expected = RibbonRuleContextHolder.current().get(attributeName);
        if (expected == null) {
            expected = defaultValue;
        }
        String  actual = server.getInstanceInfo().getMetadata().get(attributeName);
        boolean accept = expected == null || expected.equals(actual);
        log.trace("expected [{}={}] to {}[{}={}] => {}",
                  attributeName,
                  expected,
                  server.getHostPort(),
                  attributeName,
                  actual,
                  accept);
        return accept;
    }
}
