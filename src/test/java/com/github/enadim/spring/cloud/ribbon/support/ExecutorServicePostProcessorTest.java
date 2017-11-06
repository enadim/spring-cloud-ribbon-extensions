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

import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.AsyncListenableTaskExecutorPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.AsyncTaskExecutorPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutorPropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutorServicePropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ScheduledExecutorServicePropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.SchedulingTaskExecutorPropagator;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ExecutorServicePostProcessor;
import org.junit.Test;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

public class ExecutorServicePostProcessorTest {

    ExecutorServicePostProcessor processor = new ExecutorServicePostProcessor();

    @Test
    public void postProcessAfterInitialization() throws Exception {
        assertThat(processor.postProcessAfterInitialization(mock(Executor.class), "name").getClass(),
                equalTo(ExecutorPropagator.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncListenableTaskExecutor.class), "name").getClass(),
                equalTo(AsyncListenableTaskExecutorPropagator.class));
        assertThat(processor.postProcessAfterInitialization(mock(AsyncTaskExecutor.class), "name").getClass(),
                equalTo(AsyncTaskExecutorPropagator.class));
        assertThat(processor.postProcessAfterInitialization(mock(ExecutorService.class), "name").getClass(),
                equalTo(ExecutorServicePropagator.class));
        assertThat(processor.postProcessAfterInitialization(mock(ScheduledExecutorService.class), "name").getClass(),
                equalTo(ScheduledExecutorServicePropagator.class));
        assertThat(processor.postProcessAfterInitialization(mock(SchedulingTaskExecutor.class), "name").getClass(),
                equalTo(SchedulingTaskExecutorPropagator.class));
    }

}