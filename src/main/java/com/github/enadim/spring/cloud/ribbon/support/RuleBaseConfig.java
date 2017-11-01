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
package com.github.enadim.spring.cloud.ribbon.support;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
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
 * Convenient configuration initializing the ribbon client config, that is required to defining rules.
 * <p>Disables server list filter: ({@link ZonePreferenceServerListFilter}).
 * <p>Enables {@link DynamicServerListLoadBalancer}
 *
 * @author Nadim Benabdenbi
 * @see RibbonClientConfiguration
 */
@Configuration
@AutoConfigureBefore(RibbonClientConfiguration.class)
@EnableConfigurationProperties({EurekaInstanceProperties.class, RibbonClientProperties.class})
@Slf4j
public class RuleBaseConfig {
    @Autowired
    protected EurekaInstanceProperties eurekaInstanceProperties;
    @Autowired
    protected RibbonClientProperties ribbonClientProperties;

    @Bean
    @ConditionalOnMissingBean
    public ILoadBalancer loadBalancer(IClientConfig config,
                                      ServerList<Server> serverList,
                                      ServerListFilter<Server> serverListFilter,
                                      IRule rule, IPing ping,
                                      ServerListUpdater serverListUpdater) {
        log.info("dynamic server list load balancer enabled.");
        return new DynamicServerListLoadBalancer<>(config, rule, ping, serverList,
                serverListFilter, serverListUpdater);
    }

    /**
     * @return a pass through filter
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerListFilter<Server> serverListFilter() {
        log.info("ribbon discovery server list filter disabled.");
        return x -> x;
    }
}
