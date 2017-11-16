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

import com.github.enadim.spring.cloud.ribbon.predicate.StrictMetadataMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.enadim.spring.cloud.ribbon.rule.RuleDescription.from;

/**
 * The strict metadata matcher load balancing rule definition.
 * <p>Should not be imported directly for further compatibility reason: please use {@link EnableRibbonStrictMetadataMatcher}.
 *
 * @author Nadim Benabdenbi
 * @see StrictMetadataMatcher
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = "ribbon.extensions.rule.strict-matcher.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.client.${ribbon.client.name}.rule.strict-matcher.enabled:true}")
@Slf4j
public class StrictMetadataMatcherConfig extends RuleBaseConfig {

    /**
     * @param clientConfig the client config
     * @param rule         the predicate rule support
     * @return an instance of {@link StrictMetadataMatcher}
     */
    @Bean
    public StrictMetadataMatcher strictMetadataMatcher(IClientConfig clientConfig, PredicateBasedRuleSupport rule) {
        StrictMetadataMatcher strictMetadataMatcher = new StrictMetadataMatcher();
        rule.setPredicate(strictMetadataMatcher);
        rule.setDescription(from(strictMetadataMatcher));
        log.info("Strict metadata matcher enabled for client [{}].", clientConfig.getClientName());
        return strictMetadataMatcher;
    }
}
