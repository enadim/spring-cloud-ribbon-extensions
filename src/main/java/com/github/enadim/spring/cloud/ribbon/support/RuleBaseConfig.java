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

import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Convenient configuration initializing the ribbon client config that is required for defining custom rules.
 * <p>Disables server list filter: ({@link ZonePreferenceServerListFilter})
 * otherwise we may experience some weird error as {@link NullPointerException} logging.
 * <p>Replaces the default {@link ZoneAwareLoadBalancer} with its super class {@link DynamicServerListLoadBalancer}
 * because of the strong dependency with the {@link ZoneAvoidancePredicate} that leads to worst performance.
 *
 * @author Nadim Benabdenbi
 * @see RibbonClientConfiguration
 */
@Configuration
@AutoConfigureBefore(RibbonClientConfiguration.class)
@EnableConfigurationProperties(EurekaInstanceProperties.class)
@Slf4j
public class RuleBaseConfig {
    /**
     * The eureka instance properties.
     */
    @Autowired
    protected EurekaInstanceProperties eurekaInstanceProperties;

    /**
     * The load balancing rule definition.
     *
     * @return the predicate base rule: expect a single predicate defined on the context.
     */
    @Bean
    public PredicateBasedRuleSupport rule() {
        return new PredicateBasedRuleSupport();
    }

    /**
     * The load balancer definition.
     *
     * @param config            the client config.
     * @param serverList        the server list.
     * @param serverListFilter  the server list filter.
     * @param rule              the load balancing rule.
     * @param ping              the ping strategy.
     * @param serverListUpdater the server list updater.
     * @return The Dynamic Server List Load Balancer.
     */
    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer loadBalancer(IClientConfig config,
                                      ServerList<Server> serverList,
                                      ServerListFilter<Server> serverListFilter,
                                      IRule rule, IPing ping,
                                      ServerListUpdater serverListUpdater) {
        log.debug("dynamic server list load balancer enabled.");
        return new DynamicServerListLoadBalancer<>(config, rule, ping, serverList,
                serverListFilter, serverListUpdater);
    }

    /**
     * The server list filter definition.
     *
     * @return a pass-through filter.
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerListFilter<Server> serverListFilter() {
        log.debug("ribbon discovery server list filter disabled.");
        return x -> x;
    }
}
