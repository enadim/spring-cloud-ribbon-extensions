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

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ExecutionContextAwareCallable.wrap;

/**
 * Copies current {@link ExecutionContext} to delegate executor service task.
 *
 * @author Nadim Benabdenbi
 */
public class ExecutionContextAwareExecutorService extends ExecutionContextAwareExecutor implements ExecutorService {
    /**
     * The delegate executor service.
     */
    private final ExecutorService delegate;

    /**
     * Sole Constructor
     *
     * @param delegate the delegate executor service.
     */
    public ExecutionContextAwareExecutorService(@NotNull ExecutorService delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void shutdown() {
        delegate.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isShutdown() {
        return delegate.isShutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isTerminated() {
        return delegate.isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(wrap(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(ExecutionContextAwareRunnable.wrap(task), result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Future<?> submit(Runnable task) {
        return delegate.submit(ExecutionContextAwareRunnable.wrap(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(wrap(tasks));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                               long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(wrap(tasks), timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws
            InterruptedException,
            ExecutionException {
        return delegate.invokeAny(wrap(tasks));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                                 long timeout,
                                 TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(wrap(tasks), timeout, unit);
    }
}
