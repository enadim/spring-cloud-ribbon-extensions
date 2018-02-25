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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.predicate.DynamicZoneMatcher;
import com.github.enadim.spring.cloud.ribbon.predicate.ZoneAffinityMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.github.enadim.spring.cloud.ribbon.rule.RuleDescription;
import com.github.enadim.spring.cloud.ribbon.support.FavoriteZoneConfig.FavoriteZoneProperties;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.CompositePredicate.Builder;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.enadim.spring.cloud.ribbon.rule.RuleDescription.from;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.ANY_PREDICATE_DESCRIPTION;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.AVAILABILITY_PREDICATE_DESCRIPTION;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.DEFAULT_EUREKA_ZONE;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.DEFAULT_FAVORITE_ZONE_KEY;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.FAVORITE_ZONE_PREFIX;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.FAVORITE_ZONE_RULE_CLIENT_ENABLED_EXPRESSION;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.FAVORITE_ZONE_RULE_ENABLED;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.ZONE_AVOIDANCE_PREDICATE_DESCRIPTION;
import static com.netflix.loadbalancer.AbstractServerPredicate.alwaysTrue;
import static com.netflix.loadbalancer.CompositePredicate.withPredicates;

/**
 * The Favorite zone load balancing rule configuration.
 * <p>Should not be imported directly for further compatibility reason: please use {@link EnableRibbonFavoriteZone}
 * <p>Favorite Rule Definition
 * <ul>
 * <li>Start applying {@link DynamicZoneMatcher} : choose a server in the favorite zone defined in the {@link ExecutionContext}.
 * <li>Fallbacks to {@link ZoneAffinityMatcher}: choose a server in the same zone as the current instance.
 * <li>Fallbacks to {@link DynamicZoneMatcher}: choose a server in the up stream zone defined in the {@link ExecutionContext}.
 * <li>Fallbacks to {@link ZoneAffinityMatcher}: choose a server in the fallback zone.
 * <li>Fallbacks to {@link ZoneAvoidancePredicate} &amp; {@link AvailabilityPredicate}: choose an available server avoiding blacklisted zones.
 * <li>Fallbacks to {@link AvailabilityPredicate}: choose an available server.
 * <li>Fallbacks to any server
 * </ul>
 * <p><strong>Warning:</strong> Unless mastering the load balancing rules, do not mix with {@link ZonePreferenceServerListFilter} which is used by {@link DynamicServerListLoadBalancer} @see {@link #serverListFilter()}
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonFavoriteZone
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = FAVORITE_ZONE_RULE_ENABLED, matchIfMissing = true)
@ConditionalOnExpression(value = FAVORITE_ZONE_RULE_CLIENT_ENABLED_EXPRESSION)
@EnableConfigurationProperties(FavoriteZoneProperties.class)
@Slf4j
public class FavoriteZoneConfig extends RuleBaseConfig {

    /**
     * Favorite zone rule bean.
     *
     * @param clientConfig           the ribbon client config
     * @param rule                   the predicate rule support
     * @param favoriteZoneProperties the favorite zone properties
     * @param propagationProperties  the propagation properties
     * @return the favorite zone rule
     */
    @Bean
    public CompositePredicate favoriteZone(IClientConfig clientConfig,
                                           PredicateBasedRuleSupport rule,
                                           FavoriteZoneProperties favoriteZoneProperties,
                                           PropagationProperties propagationProperties) {
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(rule, clientConfig);
        ZoneAvoidancePredicate zoneAvoidancePredicate = new ZoneAvoidancePredicate(rule, clientConfig);
        ZoneAffinityMatcher zoneAffinityMatcher = new ZoneAffinityMatcher(getEurekaInstanceProperties().getZone());
        DynamicZoneMatcher dynamicZoneMatcher = new DynamicZoneMatcher(favoriteZoneProperties.getKey());
        Builder builder = withPredicates(dynamicZoneMatcher)
                .addFallbackPredicate(zoneAffinityMatcher);
        RuleDescription favoriteZoneDescription = from(dynamicZoneMatcher)
                .fallback(from(zoneAffinityMatcher));
        DynamicZoneMatcher upstreamZoneMatcher = new DynamicZoneMatcher(propagationProperties.getUpStreamZone().getKey());
        builder.addFallbackPredicate(upstreamZoneMatcher);
        favoriteZoneDescription.fallback(from(upstreamZoneMatcher));
        ZoneAffinityMatcher fallbackZoneMatcher = new ZoneAffinityMatcher(favoriteZoneProperties.getFallback());
        builder.addFallbackPredicate(fallbackZoneMatcher);
        favoriteZoneDescription.fallback(from(fallbackZoneMatcher));
        CompositePredicate favoriteZonePredicate = builder
                .addFallbackPredicate(withPredicates(zoneAvoidancePredicate, availabilityPredicate).build())
                .addFallbackPredicate(availabilityPredicate)
                .addFallbackPredicate(alwaysTrue())
                .build();
        rule.setPredicate(favoriteZonePredicate);
        rule.setDescription(favoriteZoneDescription
                .fallback(from(ZONE_AVOIDANCE_PREDICATE_DESCRIPTION).and(from(AVAILABILITY_PREDICATE_DESCRIPTION)))
                .fallback(from(AVAILABILITY_PREDICATE_DESCRIPTION))
                .fallback(from(ANY_PREDICATE_DESCRIPTION)));
        log.info("Favorite zone enabled for client:'{}' using key:'{}' upstream-key:'{}' .",
                clientConfig.getClientName(), favoriteZoneProperties.getKey());
        return favoriteZonePredicate;
    }

    @ConfigurationProperties(FAVORITE_ZONE_PREFIX)
    @Getter
    @Setter
    public static class FavoriteZoneProperties {
        private String key = DEFAULT_FAVORITE_ZONE_KEY;
        private String fallback = DEFAULT_EUREKA_ZONE;
    }
}
