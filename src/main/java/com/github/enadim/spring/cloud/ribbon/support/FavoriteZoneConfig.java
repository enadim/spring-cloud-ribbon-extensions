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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.predicate.FavoriteZoneMatcher;
import com.github.enadim.spring.cloud.ribbon.predicate.ZoneMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.netflix.loadbalancer.AbstractServerPredicate.alwaysTrue;
import static com.netflix.loadbalancer.CompositePredicate.withPredicates;

/**
 * The Favorite name load balancing rule configuration.
 * <p>Should not be imported directly for further compatibility reason: please use {@link EnableRibbonFavoriteZone}
 * <p>Favorite Rule Definition
 * <ul>
 * <li>Start applying {@link FavoriteZoneMatcher} : choose a server having the same name as the favorite name defined in the {@link RibbonRuleContext}.
 * <li>Fallbacks to {@link ZoneMatcher}: choose a server in the same name as the current instance.
 * <li>Fallbacks to {@link ZoneAvoidancePredicate} &amp; {@link AvailabilityPredicate}: choose an available server.
 * <li>Fallbacks to {@link AvailabilityPredicate}: choose an available server.
 * <li>Fallbacks to any server
 * </ul>
 * <p><strong>Warning:</strong> Unless mastering the load balancing rules, do not mix with {@link ZonePreferenceServerListFilter} which is used by {@link DynamicServerListLoadBalancer} @see {@link #serverListFilter()}
 * <p>The favorite name default metadata attribute name is 'name', however it can be configured by the property 'ribbon.rule.favorite.name.name'
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonFavoriteZone
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = "ribbon.extensions.rule.favorite-zone.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.client.${ribbon.client.name}.rule.favorite-zone.enabled:true}")
@Slf4j
public class FavoriteZoneConfig extends RuleBaseConfig {

    @Value("${ribbon.extensions.client.${ribbon.client.name}.rule.favorite-zone.key:${ribbon.extensions.rule.favorite-zone.key:zone}}")
    protected String favoriteZoneKey;

    /**
     * Favorite zone rule bean.
     *
     * @param clientConfig the ribbon client config
     * @return the favorite zone rule
     */
    @Bean
    public CompositePredicate favoriteZone(IClientConfig clientConfig) {
        PredicateBasedRuleSupport rule = new PredicateBasedRuleSupport();
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(rule, clientConfig);
        ZoneAvoidancePredicate zoneAvoidancePredicate = new ZoneAvoidancePredicate(rule, clientConfig);
        ZoneMatcher zoneMatcher = new ZoneMatcher(eurekaInstanceProperties.getZone());
        FavoriteZoneMatcher favoriteZoneMatcher = new FavoriteZoneMatcher(favoriteZoneKey);
        log.info("Enabled for client [{}] using context key [{}].", clientConfig.getClientName(), favoriteZoneKey);
        return withPredicates(favoriteZoneMatcher)
                .addFallbackPredicate(withPredicates(zoneMatcher).build())
                .addFallbackPredicate(withPredicates(zoneAvoidancePredicate, availabilityPredicate).build())
                .addFallbackPredicate(availabilityPredicate)
                .addFallbackPredicate(alwaysTrue())
                .build();
    }
}
