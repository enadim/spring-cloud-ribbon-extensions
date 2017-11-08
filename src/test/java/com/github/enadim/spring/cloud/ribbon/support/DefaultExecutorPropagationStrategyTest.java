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

import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareAsyncListenableTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareAsyncTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareScheduledExecutorService;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareSchedulingTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareThreadPoolTaskExecutor;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareThreadPoolTaskScheduler;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultExecutorPropagationStrategy;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

public class DefaultExecutorPropagationStrategyTest {

    DefaultExecutorPropagationStrategy processor = new DefaultExecutorPropagationStrategy();

    @Test
    public void postProcessAfterInitialization() throws Exception {
        //concurrent
        assertThat(processor.postProcessAfterInitialization(mock(Executor.class), "name").getClass(),
                equalTo(ExecutionContextAwareExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(ExecutorService.class), "name").getClass(),
                equalTo(ExecutionContextAwareExecutorService.class));
        assertThat(processor.postProcessAfterInitialization(mock(ScheduledExecutorService.class), "name").getClass(),
                equalTo(ExecutionContextAwareScheduledExecutorService.class));

        //spring
        assertThat(processor.postProcessAfterInitialization(mock(TaskScheduler.class), "name").getClass(),
                equalTo(ExecutionContextAwareTaskScheduler.class));
        assertThat(processor.postProcessAfterInitialization(new ThreadPoolTaskExecutor(), "name").getClass(),
                equalTo(ExecutionContextAwareThreadPoolTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(new ThreadPoolTaskScheduler(), "name").getClass(),
                equalTo(ExecutionContextAwareThreadPoolTaskScheduler.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncListenableTaskExecutor.class), "name").getClass(),
                equalTo(ExecutionContextAwareAsyncListenableTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncTaskExecutor.class), "name").getClass(),
                equalTo(ExecutionContextAwareAsyncTaskExecutor.class));
        assertThat(processor.postProcessAfterInitialization(mock(SchedulingTaskExecutor.class), "name").getClass(),
                equalTo(ExecutionContextAwareSchedulingTaskExecutor.class));
    }

}