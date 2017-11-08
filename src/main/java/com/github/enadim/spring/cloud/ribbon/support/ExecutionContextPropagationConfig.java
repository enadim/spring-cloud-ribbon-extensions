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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareAsyncListenableTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareAsyncTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareScheduledExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareSchedulingTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareThreadPoolTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareThreadPoolTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.propagator.feign.HttpHeadersCopyFeignInterceptor;
import com.github.enadim.spring.cloud.ribbon.propagator.hystrix.ExecutionContextAwareHystrixStrategy;
import com.github.enadim.spring.cloud.ribbon.propagator.jms.MessagePropertiesCopyConnectionFactoryAdapter;
import com.github.enadim.spring.cloud.ribbon.propagator.servlet.HttpHeadersCopyInterceptor;
import com.github.enadim.spring.cloud.ribbon.propagator.stomp.HeadersCopyStompSessionAdapter;
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
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Enables {@link ExecutionContext} propagation.
 * <p>Contents
 * <ul>
 * <li>Configures {@link HttpHeadersCopyFeignInterceptor} that enabled propagation to feign resource calls.
 * <li>Configures {@link HttpHeadersCopyInterceptor} that enables propagation from incoming http requests.
 * <li>Configures Hystrix to enable propagation on async commands through.
 * <li>Defines {@link ZuulHandlerMapping} post processor to enable the propagation for routes.
 * <li>Defines {@link ExecutorService} post processor to enable the propagation to async tasks.
 * <li>Defines {@link ConnectionFactory} post processor which enables the propagation from/to jms properties.
 * <li>Defines {@link StompSession} post processor which enables the propagation from/to the stomp headers.
 * </ul>
 * <p>The propagation keys should be defined on the property 'ribbon.extensions.propagation.keys' default is an empty list {}
 *
 * @author Nadim Benabdenbi
 * @see EnableExecutionContextPropagation
 */
@Slf4j
public class ExecutionContextPropagationConfig {

    /**
     * Default Feign propagation strategy based on execution context copy to the feign headers.
     */
    @Configuration
    @ConditionalOnClass(Feign.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.feign.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultFeignPropagationStrategy {
        /**
         * @param properties the propagation properties
         * @return the feign http headers interceptor
         * @see HttpHeadersCopyFeignInterceptor
         */
        @Bean
        public RequestInterceptor feignHeaderPropagator(PropagationProperties properties) {
            log.info("Propagation enabled for feign clients on keys={}.", properties.getKeysAsSet());
            return new HttpHeadersCopyFeignInterceptor(properties.getKeysAsSet()::contains);
        }
    }

    /**
     * Default inbound http request propagation strategy based on http request headers copy to the execution context.
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.http.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultInboundHttpRequestPropagationStrategy extends WebMvcConfigurerAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * Adds http request interceptor copying headers from the request to the context
         *
         * @param registry the interceptor registry
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HttpHeadersCopyInterceptor(properties.getKeysAsSet()::contains)).addPathPatterns(
                    "/**");
            log.debug("Propagation enabled for http request on keys={}.", properties.getKeysAsSet());
        }
    }

    /**
     * Default Hystrix execution context propagation strategy.
     * <p>Registers Hystrix concurrent strategy wrapping the callable see: <a href="https://github.com/Netflix/Hystrix/wiki/Plugins#concurrency-strategy)">Histrix Wiki</a>.
     *
     * @see ExecutionContextAwareHystrixStrategy
     */
    @Configuration
    @ConditionalOnClass(Hystrix.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.hystrix.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    //HystrixSecurityAutoConfiguration does not comply with a clean registration process over a static holder.
    @AutoConfigureAfter(HystrixSecurityAutoConfiguration.class)
    @Slf4j
    public static class DefaultHystrixContextPropagationStrategy {

        /**
         * Decorates the current Hystric concurrent strategy.
         */
        @PostConstruct
        public void postContruct() {
            init();
        }

        /**
         * registers the {@link ExecutionContextAwareHystrixStrategy}
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
            HystrixPlugins.getInstance().registerConcurrencyStrategy(new ExecutionContextAwareHystrixStrategy(existingConcurrencyStrategy));
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            log.info("Propagation enabled for Hystrix.");
        }
    }

    /**
     * Default Zuul propagation strategy based on http request headers copy to the execution context.
     */
    @Configuration
    @ConditionalOnClass(ZuulHandlerMapping.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.zuul.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultZuulPropagationStrategy extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) {
            if (bean instanceof ZuulHandlerMapping) {
                ZuulHandlerMapping zuulHandlerMapping = (ZuulHandlerMapping) bean;
                zuulHandlerMapping.setInterceptors(new HttpHeadersCopyInterceptor(properties.getKeysAsSet()::contains));
                log.debug("Propagation enabled for zuul handler[{}] on keys={}.", beanName, properties.getKeysAsSet());
            }
            return super.postProcessAfterInstantiation(bean, beanName);
        }
    }

    /**
     * Default executor propagation strategy that will decorate any executor found in the context.
     * <p>Support for java and spring executor: requires however that your dependency injection is base on the interface and not the implementation.
     */
    @Configuration
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.executor.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultExecutorPropagationStrategy extends InstantiationAwareBeanPostProcessorAdapter {
        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof Executor || bean instanceof TaskScheduler) {
                if (bean instanceof AsyncListenableTaskExecutor && bean instanceof SchedulingTaskExecutor && bean instanceof TaskScheduler) {
                    log.debug("Propagation enabled for ~ThreadPoolTaskScheduler [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareThreadPoolTaskScheduler((AsyncListenableTaskExecutor) bean, (SchedulingTaskExecutor) bean, (TaskScheduler) bean);
                } else if (bean instanceof AsyncListenableTaskExecutor && bean instanceof SchedulingTaskExecutor) {
                    log.debug("Propagation enabled for ~ThreadPoolTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareThreadPoolTaskExecutor((AsyncListenableTaskExecutor) bean, (SchedulingTaskExecutor) bean);
                } else if (bean instanceof TaskScheduler) {
                    log.debug("Propagation enabled for TaskScheduler [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareTaskScheduler((TaskScheduler) bean);
                } else if (bean instanceof SchedulingTaskExecutor) {
                    log.debug("Propagation enabled for SchedulingTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareSchedulingTaskExecutor((SchedulingTaskExecutor) bean);
                } else if (bean instanceof AsyncListenableTaskExecutor) {
                    log.debug("Propagation enabled for AsyncListenableTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareAsyncListenableTaskExecutor((AsyncListenableTaskExecutor) bean);
                } else if (bean instanceof AsyncTaskExecutor) {
                    log.debug("Propagation enabled for AsyncTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareAsyncTaskExecutor((AsyncTaskExecutor) bean);
                } else if (bean instanceof ScheduledExecutorService) {
                    log.debug("Propagation enabled for ScheduledExecutorService [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareScheduledExecutorService((ScheduledExecutorService) bean);
                } else if (bean instanceof ExecutorService) {
                    log.debug("Propagation enabled for ExecutorService [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ExecutionContextAwareExecutorService((ExecutorService) bean);
                } else {
                    log.debug("Propagation enabled for Executor [{}]:[{}].", bean, bean.getClass().getName());
                    return new ExecutionContextAwareExecutor((Executor) bean);
                }
            }
            return bean;
        }
    }

    /**
     * Default Stomp propagation strategy based on stomp headers copy from/to the execution context.
     */
    @Configuration
    @ConditionalOnClass(StompSession.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.stomp.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultStompPropagationStrategy extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof StompSession && !(bean instanceof HeadersCopyStompSessionAdapter)) {
                log.debug("Propagation enabled for stomp session [{}].", bean);
                return new HeadersCopyStompSessionAdapter((StompSession) bean, properties.getKeysAsSet()::contains);
            } else {
                return bean;
            }
        }
    }

    /**
     * Default jms propagation strategy based on jms properties copy from/to the execution context.
     */
    @Configuration
    @ConditionalOnClass(MessagePropertiesCopyConnectionFactoryAdapter.class)
    @ConditionalOnProperty(value = "ribbon.extensions.propagation.jms.enabled", matchIfMissing = true)
    @ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
    public static class DefaultConnectionFactoryPropagationStrategy extends InstantiationAwareBeanPostProcessorAdapter {
        @Autowired
        protected PropagationProperties properties;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof ConnectionFactory && !(bean instanceof MessagePropertiesCopyConnectionFactoryAdapter)) {
                log.debug("Propagation enabled for jms connection factory [{}].", bean);
                return new MessagePropertiesCopyConnectionFactoryAdapter((ConnectionFactory) bean, properties.getKeysAsSet()::contains);
            } else {
                return bean;
            }
        }
    }

    /**
     * The propagation properties.
     */
    @ConfigurationProperties(prefix = "ribbon.extensions.propagation")
    @Component
    @Getter
    public static class PropagationProperties {
        /**
         * the keys to copy.
         */
        private List<String> keys = new ArrayList<>();

        /**
         * Convenient keys getter (set conversion not supported for now).
         *
         * @return the keys to copy as a {@link Set}
         */
        public Set<String> getKeysAsSet() {
            return new HashSet<>(getKeys());
        }
    }
}
