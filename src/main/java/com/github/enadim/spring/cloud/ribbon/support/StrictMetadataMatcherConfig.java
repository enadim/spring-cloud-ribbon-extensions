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
package com.github.enadim.spring.cloud.ribbon.support;

import com.github.enadim.spring.cloud.ribbon.predicate.StrictMetadataMatcher;
import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The strict metadata matcher load balancing rule definition
 * <p>Uses in conjunction the {@link AvailabilityPredicate} and the {@link StrictMetadataMatcher}
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonStrictMetadataMatcher
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = "ribbon.rule.strict.matcher.enabled", matchIfMissing = true)
@Slf4j
public class StrictMetadataMatcherConfig extends RuleBaseConfig {

    @Bean
    public PredicateBasedRuleSupport strictMetadataMatcherRule(IClientConfig clientConfig) {
        PredicateBasedRuleSupport rule = new PredicateBasedRuleSupport();
        StrictMetadataMatcher strictMetadataMatcher = new StrictMetadataMatcher();
        rule.setPredicate(strictMetadataMatcher);
        log.info("Strict meta data matcher rule enabled.");
        return rule;
    }


}
