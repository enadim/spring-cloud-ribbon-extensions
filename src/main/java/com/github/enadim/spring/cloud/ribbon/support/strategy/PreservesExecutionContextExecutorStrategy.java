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

import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareAsyncListenableTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareAsyncTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareScheduledExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareSchedulingTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareThreadPoolTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareThreadPoolTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Default executor propagation strategy that will decorate any executor found in the context.
 * <p>Support for java and spring executor: requires however that your dependency injection is base on the interface and not the implementation.
 */
@Configuration
@ConditionalOnProperty(value = "ribbon.extensions.propagation.executor.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
@Slf4j
public class PreservesExecutionContextExecutorStrategy extends InstantiationAwareBeanPostProcessorAdapter {
    @Autowired
    @Setter
    private PropagationProperties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Executor || bean instanceof TaskScheduler) {
            if (properties.getExecutor().accept(beanName)) {
                if (bean instanceof AsyncListenableTaskExecutor && bean instanceof SchedulingTaskExecutor && bean instanceof TaskScheduler) {
                    log.info("Context propagation enabled for ~ThreadPoolTaskScheduler [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareThreadPoolTaskScheduler((AsyncListenableTaskExecutor) bean, (SchedulingTaskExecutor) bean, (TaskScheduler) bean);
                } else if (bean instanceof AsyncListenableTaskExecutor && bean instanceof SchedulingTaskExecutor) {
                    log.info("Context propagation enabled for ~ThreadPoolTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareThreadPoolTaskExecutor((AsyncListenableTaskExecutor) bean, (SchedulingTaskExecutor) bean);
                } else if (bean instanceof TaskScheduler) {
                    log.info("Context propagation enabled for TaskScheduler [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareTaskScheduler((TaskScheduler) bean);
                } else if (bean instanceof SchedulingTaskExecutor) {
                    log.info("Context propagation enabled for SchedulingTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareSchedulingTaskExecutor((SchedulingTaskExecutor) bean);
                } else if (bean instanceof AsyncListenableTaskExecutor) {
                    log.info("Context propagation enabled for AsyncListenableTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareAsyncListenableTaskExecutor((AsyncListenableTaskExecutor) bean);
                } else if (bean instanceof AsyncTaskExecutor) {
                    log.info("Context propagation enabled for AsyncTaskExecutor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareAsyncTaskExecutor((AsyncTaskExecutor) bean);
                } else if (bean instanceof ScheduledExecutorService) {
                    log.info("Context propagation enabled for ScheduledExecutorService [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareScheduledExecutorService((ScheduledExecutorService) bean);
                } else if (bean instanceof ExecutorService) {
                    log.info("Context propagation enabled for ExecutorService [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareExecutorService((ExecutorService) bean);
                } else {
                    log.info("Context propagation enabled for Executor [{}]:[{}].", beanName, bean.getClass().getName());
                    return new ContextAwareExecutor((Executor) bean);
                }
            } else {
                log.debug("Context propagation disabled for Executor [{}]", beanName);
            }
        }
        return bean;
    }
}
