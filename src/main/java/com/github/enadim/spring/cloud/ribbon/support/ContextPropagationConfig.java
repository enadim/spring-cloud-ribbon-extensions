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
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutorServicePropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.feign.FeignHttpHeadersPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.hystrix.HystrixPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.propagator.jms.ConnectionFactoryPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.servlet.HttpRequestHeadersPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.stomp.StompSessionPropagator;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.hystrix.security.HystrixSecurityAutoConfiguration;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Enables {@link RibbonRuleContext} propagation.
 * <p>Contents
 * <ul>
 * <li>Configures {@link FeignHttpHeadersPropagator} that enabled propagation to feign resource calls.
 * <li>Configures {@link HttpRequestHeadersPropagator} that enables propagation from incoming http requests.
 * <li>Configures Hystrix to enable propagation on async commands through.
 * <li>Defines {@link ZuulHandlerMapping} post processor to enable the propagation for routes.
 * <li>Defines {@link ExecutorService} post processor to enable the propagation to async tasks.
 * <li>Defines {@link ConnectionFactory} post processor which enables the propagation from/to jms properties.
 * <li>Defines {@link StompSession} post processor which enables the propagation from/to the stomp headers.
 * </ul>
 * <p>The propagation keys should be defined on the property 'ribbon.extensions.propagation.keys' default is an empty list {}
 *
 * @author Nadim Benabdenbi
 * @see EnableRibbonContextPropagation
 */
@Slf4j
public class ContextPropagationConfig {

    @Configuration
    @ConditionalOnClass(Feign.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.feign.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class FeignPropagationConfig {
        /**
         * @param properties the propagation properties
         * @return the feign http headers interceptor
         * @see FeignHttpHeadersPropagator
         */
        @Bean
        public RequestInterceptor feignHeaderPropagator(PropagationProperties properties) {
            log.info("Propagation enabled for feign clients on keys={}.", properties.getKeysAsSet());
            return new FeignHttpHeadersPropagator(properties.getKeysAsSet());
        }
    }

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.http.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class WebApplicationPropagationConfig extends WebMvcConfigurerAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * Adds http request interceptor copying headers from the request to the context
         *
         * @param registry the interceptor registry
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HttpRequestHeadersPropagator(properties.getKeysAsSet())).addPathPatterns(
                    "/**");
            log.debug("Propagation enabled for http request on keys={}.", properties.getKeysAsSet());
        }
    }

    /**
     * Enables {@link RibbonRuleContext} propagation with {@link Hystrix}.
     * ²     * <p>Registers Hystrix concurrent strategy wrapping the callable see: <a href="https://github.com/Netflix/Hystrix/wiki/Plugins#concurrency-strategy)">Histrix Wiki</a>.
     *
     * @see HystrixPropagationStrategy
     */
    @Configuration
    @ConditionalOnClass(Hystrix.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.hystrix.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    //HystrixSecurityAutoConfiguration does not comply with a clean registration process over a static holder.
    @AutoConfigureAfter(HystrixSecurityAutoConfiguration.class)
    @Slf4j
    public static class HystrixRibbonContextPropagationConfig {

        @PostConstruct
        public void postContruct() {
            init();
        }

        /**
         * registers the {@link HystrixPropagationStrategy}
         */
        public static void init() {
            // keeps references of existing Hystrix plugins.
            HystrixConcurrencyStrategy existingConcurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            // reset the Hystrix plugin
            HystrixPlugins.reset();
            // configure the  plugin
            HystrixPlugins.getInstance().registerConcurrencyStrategy(new HystrixPropagationStrategy(existingConcurrencyStrategy));
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            log.info("Propagation enabled for Hystrix.");
        }
    }

    /**
     * Zuul support
     */
    @Configuration
    @ConditionalOnClass(ZuulHandlerMapping.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.zuul.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class ZuulHandlerBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) {
            if (bean instanceof ZuulHandlerMapping) {
                ZuulHandlerMapping zuulHandlerMapping = (ZuulHandlerMapping) bean;
                zuulHandlerMapping.setInterceptors(new HttpRequestHeadersPropagator(properties.getKeysAsSet()));
                log.debug("Propagation enabled for zuul handler[{}] on keys={}.", beanName, properties.getKeysAsSet());
            }
            return super.postProcessAfterInstantiation(bean, beanName);
        }
    }

    /**
     * {@link ExecutorService} support
     */
    @Configuration
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.executor.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class ExecutorServicePostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof ExecutorService && !(bean instanceof ExecutorServicePropagator)) {
                log.debug("Propagation enabled for executor[{}].", bean);
                return new ExecutorServicePropagator((ExecutorService) bean);
            } else {
                return bean;
            }
        }
    }

    /**
     * Stomp {@link StompSession} support.
     */
    @Configuration
    @ConditionalOnClass(StompSession.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.stomp.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class StompPropagationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof StompSession && !(bean instanceof StompSessionPropagator)) {
                log.debug("Propagation enabled for stomp session [{}].", bean);
                return new StompSessionPropagator((StompSession) bean, properties.getKeysAsSet());
            } else {
                return bean;
            }
        }
    }

    /**
     * Jms propagation support.
     */
    @Configuration
    @ConditionalOnClass(ConnectionFactoryPropagator.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.jms.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class ConnectionFactoryPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof ConnectionFactory && !(bean instanceof ConnectionFactoryPropagator)) {
                log.debug("Propagation enabled for jms connection factory [{}].", bean);
                return new ConnectionFactoryPropagator((ConnectionFactory) bean, properties.getKeysAsSet());
            } else {
                return bean;
            }
        }
    }

    @ConfigurationProperties(prefix = "ribbon.extensions.propagation")
    @Component
    @Getter
    public static class PropagationProperties {
        /**
         * the keys to propagate
         */
        private List<String> keys = new ArrayList<>();

        /**
         * Convenient keys getter (set conversion not supported for now).
         *
         * @return the keys to propagate as a {@link Set}
         */
        public Set<String> getKeysAsSet() {
            return new HashSet<>(getKeys());
        }
    }
}
