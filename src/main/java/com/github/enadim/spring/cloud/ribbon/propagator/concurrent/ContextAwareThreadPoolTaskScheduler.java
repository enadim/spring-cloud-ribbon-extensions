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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Copies current {@link ExecutionContext} to delegate composite scheduler of {@link AsyncListenableTaskExecutor} and {@link SchedulingTaskExecutor} and {@link TaskScheduler}.
 * <p>{@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler} use case.
 *
 * @author Nadim Benabdenbi
 */
public class ContextAwareThreadPoolTaskScheduler extends ContextAwareThreadPoolTaskExecutor implements TaskScheduler {
    /**
     * The delegate task scheduler propagator.
     */
    private final ContextAwareTaskScheduler executionContextAwareTaskScheduler;

    /**
     * Sole Constructor.
     *
     * @param asyncListenableTaskExecutor The delegate async listenable task executor.
     * @param schedulingTaskExecutor      The delegate scheduling task executor.
     * @param taskScheduler               the delegate task scheduler.
     */
    public ContextAwareThreadPoolTaskScheduler(@NotNull AsyncListenableTaskExecutor asyncListenableTaskExecutor,
                                               @NotNull SchedulingTaskExecutor schedulingTaskExecutor,
                                               @NotNull TaskScheduler taskScheduler) {
        super(asyncListenableTaskExecutor, schedulingTaskExecutor);
        executionContextAwareTaskScheduler = new ContextAwareTaskScheduler(taskScheduler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return executionContextAwareTaskScheduler.schedule(task, trigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return executionContextAwareTaskScheduler.schedule(task, startTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return executionContextAwareTaskScheduler.scheduleAtFixedRate(task, startTime, period);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return executionContextAwareTaskScheduler.scheduleAtFixedRate(task, period);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return executionContextAwareTaskScheduler.scheduleWithFixedDelay(task, startTime, delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return executionContextAwareTaskScheduler.scheduleWithFixedDelay(task, delay);
    }
}
