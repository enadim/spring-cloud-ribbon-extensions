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
package com.github.enadim.spring.cloud.ribbon.support.strategy;

import com.github.enadim.spring.cloud.ribbon.propagator.feign.PreservesHttpHeadersFeignInterceptor;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default Feign propagation strategy based on execution context copy to the feign headers.
 */
@Configuration
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(value = "ribbon.extensions.propagation.feign.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
@Slf4j
public class PreservesHttpHeadersFeignStrategy {
    @Autowired
    private PropagationProperties properties;
    @Autowired
    private EurekaInstanceProperties eurekaInstanceProperties;

    /**
     * @return the feign http headers interceptor
     * @see PreservesHttpHeadersFeignInterceptor
     */
    @Bean
    public RequestInterceptor feignPropagator() {
        log.info("Context propagation enabled for feign clients on keys={}: url-includes{},url-excludes{}", properties.getKeys(), properties.getFeign().getIncludes(), properties.getFeign().getExcludes());
        return new PreservesHttpHeadersFeignInterceptor(properties.getFeign(),
                properties.buildEntriesFilter(),
                properties.buildExtraStaticEntries(eurekaInstanceProperties));
    }
}
