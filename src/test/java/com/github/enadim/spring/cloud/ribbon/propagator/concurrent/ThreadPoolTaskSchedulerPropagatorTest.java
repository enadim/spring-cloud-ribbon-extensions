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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ThreadPoolTaskSchedulerPropagatorTest extends AbstractExecutorPropagatorTest {
    private TaskScheduler taskScheduler = mock(TaskScheduler.class);
    private SchedulingTaskExecutor schedulingTaskExecutor = mock(SchedulingTaskExecutor.class);
    private AsyncListenableTaskExecutor asyncListenableTaskExecutor = mock(AsyncListenableTaskExecutor.class);
    private ThreadPoolTaskSchedulerPropagator propagator = new ThreadPoolTaskSchedulerPropagator(asyncListenableTaskExecutor, schedulingTaskExecutor, taskScheduler);

    @Test
    public void scheduleTrigger() throws Exception {
        propagator.schedule(runnable, trigger);
        verify(taskScheduler).schedule(any(PropagationRunnable.class), any(Trigger.class));
    }

    @Test
    public void scheduleDate() throws Exception {
        propagator.schedule(runnable, date);
        verify(taskScheduler).schedule(any(PropagationRunnable.class), eq(date));
    }

    @Test
    public void scheduleAtFixedRateWithDate() throws Exception {
        propagator.scheduleAtFixedRate(runnable, date, period);
        verify(taskScheduler).scheduleAtFixedRate(any(PropagationRunnable.class), eq(date), eq(period));
    }

    @Test
    public void scheduleAtFixedRate() throws Exception {
        propagator.scheduleAtFixedRate(runnable, period);
        verify(taskScheduler).scheduleAtFixedRate(any(PropagationRunnable.class), eq(period));
    }

    @Test
    public void scheduleWithFixedDelayWithDate() throws Exception {
        propagator.scheduleWithFixedDelay(runnable, date, period);
        verify(taskScheduler).scheduleWithFixedDelay(any(PropagationRunnable.class), eq(date), eq(period));
    }

    @Test
    public void scheduleWithFixedDelay() throws Exception {
        propagator.scheduleWithFixedDelay(runnable, period);
        verify(taskScheduler).scheduleWithFixedDelay(any(PropagationRunnable.class), eq(period));
    }

}