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

import com.github.enadim.spring.cloud.ribbon.propagator.servlet.PreservesHttpHeadersInterceptor;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Default inbound http request propagation strategy based on http request headers copy to the execution context.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "ribbon.extensions.propagation.http.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
@Slf4j
public class PreservesHeadersInboundHttpRequestStrategy extends WebMvcConfigurerAdapter {
    @Autowired
    private PropagationProperties properties;

    /**
     * Adds http request interceptor copying headers from the request to the context
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PreservesHttpHeadersInterceptor(properties.buildEntriesFilter())).addPathPatterns(
                "/**");
        log.debug("Context propagation enabled for http request on keys={}.", properties.getKeys());
    }
}
