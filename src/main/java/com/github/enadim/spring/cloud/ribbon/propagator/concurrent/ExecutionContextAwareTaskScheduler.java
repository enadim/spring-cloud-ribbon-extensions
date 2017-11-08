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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Copies current {@link ExecutionContext} to delegate scheduler task.
 *
 * @author Nadim Benabdenbi
 */
public class ExecutionContextAwareTaskScheduler implements TaskScheduler {

    /**
     * The delegate task scheduler.
     */
    private final TaskScheduler delegate;

    /**
     * Sole Constructor.
     *
     * @param delegate the delegate task scheduler.
     */
    public ExecutionContextAwareTaskScheduler(@NotNull TaskScheduler delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return delegate.schedule(ExecutionContextAwareRunnable.wrap(task), trigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return delegate.schedule(ExecutionContextAwareRunnable.wrap(task), startTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return delegate.scheduleAtFixedRate(ExecutionContextAwareRunnable.wrap(task), startTime, period);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return delegate.scheduleAtFixedRate(ExecutionContextAwareRunnable.wrap(task), period);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return delegate.scheduleWithFixedDelay(ExecutionContextAwareRunnable.wrap(task), startTime, delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return delegate.scheduleWithFixedDelay(ExecutionContextAwareRunnable.wrap(task), delay);
    }
}
