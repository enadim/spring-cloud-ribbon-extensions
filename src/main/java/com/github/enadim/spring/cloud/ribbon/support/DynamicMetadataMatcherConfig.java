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

import com.github.enadim.spring.cloud.ribbon.predicate.DynamicMetadataMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.enadim.spring.cloud.ribbon.rule.RuleDescription.from;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.DYNAMIC_METADATA_MATCHER_RULE_CLIENT_ENABLED_EXPRESSION;
import static com.github.enadim.spring.cloud.ribbon.support.RibbonExtensionsConstants.DYNAMIC_METADATA_MATCHER_RULE_ENABLED;

/**
 * The dynamic attribute metadata matcher load balancing rule definition.
 * <p>Should not be imported directly for further compatibility reason: please use {@link EnableRibbonDynamicMetadataMatcher}.
 *
 * @author Nadim Benabdenbi
 * @see DynamicMetadataMatcher
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = DYNAMIC_METADATA_MATCHER_RULE_ENABLED, matchIfMissing = true)
@ConditionalOnExpression(value = DYNAMIC_METADATA_MATCHER_RULE_CLIENT_ENABLED_EXPRESSION)
@Slf4j
public class DynamicMetadataMatcherConfig extends RuleBaseConfig {

    /**
     * The dynamic key
     */
    @Value("${ribbon.extensions.client.${ribbon.client.name}.rule.dynamic-metadata-matcher.key:${ribbon.extensions.rule.dynamic-metadata-matcher.key:dynamic-matcher-key}}")
    private String key;

    /**
     * The dynamic key
     */
    @Value("${ribbon.extensions.client.${ribbon.client.name}.rule.dynamic-metadata-matcher.matchIfMissing:${ribbon.extensions.rule.dynamic-metadata-matcher.matchIfMissing:true}}")
    private boolean matchIfMissing;

    /**
     * @param clientConfig the client config.
     * @param rule         the predicate rule support.
     * @return an instance of {@link DynamicMetadataMatcher}
     */
    @Bean
    public DynamicMetadataMatcher dynamicMetadaMatcher(IClientConfig clientConfig, PredicateBasedRuleSupport rule) {
        DynamicMetadataMatcher dynamicMetadataMatcher = new DynamicMetadataMatcher(key, matchIfMissing);
        rule.setPredicate(dynamicMetadataMatcher);
        rule.setDescription(from(dynamicMetadataMatcher));
        log.info("Dynamic matcher rule enabled for client [{}] using dynamic key[{}].", clientConfig.getClientName(), key);
        return dynamicMetadataMatcher;
    }
}
