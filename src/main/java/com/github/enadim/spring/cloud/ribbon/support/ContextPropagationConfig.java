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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.propagator.ExecutorServicePropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.FeignHttpHeadersPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.HttpRequestHeadersPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.PropagationJmsMessagePostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.WebApplicationPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ZuulHandlerBeanPostProcessor;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static java.util.Arrays.asList;

/**
 * Enables {@link RibbonRuleContext} attributes propagation.
 * <p>Contents
 * <ul>
 * <li>Configures {@link FeignHttpHeadersPropagator} that enabled propagation to feign resource calls.
 * <li>Configures {@link HttpRequestHeadersPropagator} that enables propagation from incoming http requests.
 * <li>Defines a convenience factory method {@link #wrapExecutorService(ExecutorService)} that enables the propagation to async tasks.
 * <li>Defines a jms message post processor that enables the propagation to jms properties
 * </ul>
 * <p>The propagation attributes attributes should be defined on the property 'ribbon.rule.propagation.attributes' default is {'name'}
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonContextPropagation
 */
@Configuration
@ConditionalOnProperty(value = "ribbon.rule.propagation.enabled", matchIfMissing = true)
@Import({WebApplicationPropagationConfig.class, ZuulHandlerBeanPostProcessor.class})
@EnableHystrixRibbonContextPropagation
@Slf4j
public class ContextPropagationConfig {
    @Autowired
    PropagationProperties properties;

    /**
     * @return the feign http headers interceptor
     * @see FeignHttpHeadersPropagator
     */
    @Bean
    @ConditionalOnClass(Feign.class)
    public RequestInterceptor feignHeaderPropagator() {
        log.info("propagation enabled for feign clients {}.", properties.getAttributesAsSet());
        return new FeignHttpHeadersPropagator(properties.getAttributesAsSet());
    }

    /**
     * Enables context propagation for an executor service
     *
     * @param executorService the executor service to wrap in order to enable propagation
     * @return the wrapped executor service
     * @see ExecutorServicePropagator
     */
    public static ExecutorService wrapExecutorService(ExecutorService executorService) {
        return new ExecutorServicePropagator(executorService);
    }

    /**
     * {@link MessagePostProcessor} bean that propagate the context to jms properties.
     *
     * @return {@link PropagationJmsMessagePostProcessor} instance.
     */
    @Bean(name = "propagationJmsMessagePostProcessor")
    public MessagePostProcessor propagationJmsMessagePostProcessor() {
        return new PropagationJmsMessagePostProcessor(properties.getAttributesAsSet());
    }

    @Configuration
    @ConditionalOnWebApplication
    public static class WebApplicationPropagationConfig extends WebMvcConfigurerAdapter {
        @Autowired
        PropagationProperties properties;

        /**
         * Adds http request interceptor copying headers from the request to the context
         *
         * @param registry the interceptor registry
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HttpRequestHeadersPropagator(properties.getAttributesAsSet())).addPathPatterns("/**");
            log.info("propagation enabled for rest controllers {}.", properties.getAttributesAsSet());
        }
    }

    @Configuration
    @ConditionalOnClass(ZuulHandlerMapping.class)
    public static class ZuulHandlerBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        PropagationProperties properties;

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (bean instanceof ZuulHandlerMapping) {
                ZuulHandlerMapping zuulHandlerMapping = (ZuulHandlerMapping) bean;
                zuulHandlerMapping.setInterceptors(new HttpRequestHeadersPropagator(properties.getAttributesAsSet()));
                log.info("propagation enabled for zuul handler {} on {}.", beanName, properties.getAttributesAsSet());
            }
            return super.postProcessAfterInstantiation(bean, beanName);
        }
    }

    @ConfigurationProperties(prefix = "ribbon.rule.propagation")
    @Component
    @Getter
    public static class PropagationProperties {
        private boolean enabled = true;
        private List<String> attributes = new ArrayList<>(asList("zone"));

        public Set<String> getAttributesAsSet() {
            return new HashSet<>(attributes);
        }
    }
}
