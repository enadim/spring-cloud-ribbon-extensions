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
package com.github.enadim.spring.cloud.ribbon.propagator.concurrent;

import org.junit.Test;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ThreadPoolTaskExecutorPropagatorTest extends AbstractExecutorPropagatorTest {
    private SchedulingTaskExecutor schedulingTaskExecutor = mock(SchedulingTaskExecutor.class);
    private AsyncListenableTaskExecutor asyncListenableTaskExecutor = mock(AsyncListenableTaskExecutor.class);
    private ThreadPoolTaskExecutorPropagator propagator = new ThreadPoolTaskExecutorPropagator(asyncListenableTaskExecutor, schedulingTaskExecutor);
    private long period = 1;

    @Test
    public void execute() throws Exception {
        propagator.execute(runnable, period);
        verify(schedulingTaskExecutor).execute(any(PropagationRunnable.class), eq(period));
    }

    @Test
    public void submit() throws Exception {
        propagator.submit(runnable);
        verify(schedulingTaskExecutor).submit(any(PropagationRunnable.class));
    }

    @Test
    public void submit1() throws Exception {
        propagator.submit(callable);
        verify(schedulingTaskExecutor).submit(any(PropagationCallable.class));
    }

    @Test
    public void prefersShortLivedTasks() throws Exception {
        propagator.prefersShortLivedTasks();
        verify(schedulingTaskExecutor).prefersShortLivedTasks();
    }

    @Test
    public void execute1() throws Exception {
        propagator.execute(runnable);
        verify(asyncListenableTaskExecutor).execute(any(PropagationRunnable.class));
    }

    @Test
    public void submitListenable() throws Exception {
        propagator.submitListenable(runnable);
        verify(asyncListenableTaskExecutor).submitListenable(any(PropagationRunnable.class));
    }

    @Test
    public void submitListenable1() throws Exception {
        propagator.submitListenable(callable);
        verify(asyncListenableTaskExecutor).submitListenable(any(PropagationCallable.class));
    }

}