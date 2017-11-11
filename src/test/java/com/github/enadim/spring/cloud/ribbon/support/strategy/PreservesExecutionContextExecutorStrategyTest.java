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
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

public class PreservesExecutionContextExecutorStrategyTest {
    String beanName = "bean";
    String toBeExcluded = "toBeExcluded";
    PreservesExecutionContextExecutorStrategy processor = new PreservesExecutionContextExecutorStrategy();

    @Before
    public void before() {
        PropagationProperties properties = new PropagationProperties();
        properties.getExecutor().getExcludes().add(Pattern.compile(toBeExcluded));
        processor.setProperties(properties);
    }

    @Test
    public void postProcessAfterInitialization() throws Exception {
        assertThat(processor.postProcessAfterInitialization(mock(Executor.class), toBeExcluded).getClass(),
                not(equalTo(ContextAwareExecutor.class)));
        //concurrent
        assertThat(processor.postProcessAfterInitialization(mock(Executor.class), beanName).getClass(),
                equalTo(ContextAwareExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(ExecutorService.class), beanName).getClass(),
                equalTo(ContextAwareExecutorService.class));
        assertThat(processor.postProcessAfterInitialization(mock(ScheduledExecutorService.class), beanName).getClass(),
                equalTo(ContextAwareScheduledExecutorService.class));

        //spring
        assertThat(processor.postProcessAfterInitialization(mock(TaskScheduler.class), beanName).getClass(),
                equalTo(ContextAwareTaskScheduler.class));
        assertThat(processor.postProcessAfterInitialization(new ThreadPoolTaskExecutor(), beanName).getClass(),
                equalTo(ContextAwareThreadPoolTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(new ThreadPoolTaskScheduler(), beanName).getClass(),
                equalTo(ContextAwareThreadPoolTaskScheduler.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncListenableTaskExecutor.class), beanName).getClass(),
                equalTo(ContextAwareAsyncListenableTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncTaskExecutor.class), beanName).getClass(),
                equalTo(ContextAwareAsyncTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(SchedulingTaskExecutor.class), beanName).getClass(),
                equalTo(ContextAwareSchedulingTaskExecutor.class));
    }
}