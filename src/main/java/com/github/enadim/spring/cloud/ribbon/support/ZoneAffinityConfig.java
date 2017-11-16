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
package com.github.enadim.spring.cloud.ribbon.support;

import com.github.enadim.spring.cloud.ribbon.predicate.ZoneAffinityMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.enadim.spring.cloud.ribbon.rule.RuleDescription.from;
import static com.netflix.loadbalancer.AbstractServerPredicate.alwaysTrue;
import static com.netflix.loadbalancer.CompositePredicate.withPredicates;

/**
 * The Zone Affinity load balancing rule configuration.
 * <p>Should not be imported directly for further compatibility reason: please use {@link EnableRibbonZoneAffinity}
 * <p>Zone Affinity Rule Definition
 * <ul>
 * <li>Fallbacks to {@link ZoneAffinityMatcher}: choose a server in the same description as the current instance.
 * <li>Fallbacks to {@link ZoneAvoidancePredicate} &amp; {@link AvailabilityPredicate}: choose an available server.
 * <li>Fallbacks to {@link AvailabilityPredicate}: choose an available server.
 * <li>Fallbacks to any server
 * </ul>
 * <p><strong>Warning:</strong> Unless mastering the load balancing rules, do not mix with {@link ZonePreferenceServerListFilter} which is used by {@link DynamicServerListLoadBalancer} @see {@link #serverListFilter()}
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonZoneAffinity
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = "ribbon.extensions.rule.zone-affinity.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.client.${ribbon.client.name}.rule.zone-affinity.enabled:true}")
@Slf4j
public class ZoneAffinityConfig extends RuleBaseConfig {

    /**
     * Zone affinity rule.
     *
     * @param clientConfig the ribbon client config.
     * @param rule         the predicate rule support
     * @return the zone affinity rule.
     */
    @Bean
    public CompositePredicate zoneAffinity(IClientConfig clientConfig, PredicateBasedRuleSupport rule) {
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(rule, clientConfig);
        ZoneAvoidancePredicate zoneAvoidancePredicate = new ZoneAvoidancePredicate(rule, clientConfig);
        ZoneAffinityMatcher zoneAffinityMatcher = new ZoneAffinityMatcher(getEurekaInstanceProperties().getZone());
        CompositePredicate predicate = withPredicates(zoneAffinityMatcher)
                .addFallbackPredicate(withPredicates(zoneAvoidancePredicate, availabilityPredicate).build())
                .addFallbackPredicate(availabilityPredicate)
                .addFallbackPredicate(alwaysTrue())
                .build();
        rule.setPredicate(predicate);
        rule.setDescription(from(zoneAffinityMatcher)
                .fallback(from("ZoneAvoidance").and(from("Availability")))
                .fallback(from("Availability"))
                .fallback(from("Any()")));
        log.info("Zone affinity enabled for client [{}].", clientConfig.getClientName());
        return predicate;
    }
}
