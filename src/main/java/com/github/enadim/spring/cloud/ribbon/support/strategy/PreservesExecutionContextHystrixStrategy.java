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

import com.github.enadim.spring.cloud.ribbon.propagator.hystrix.ExecutionContextAwareHystrixStrategy;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.hystrix.security.HystrixSecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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
public class PreservesExecutionContextHystrixStrategy {

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
        log.info("Context propagation enabled for Hystrix.");
    }
}
