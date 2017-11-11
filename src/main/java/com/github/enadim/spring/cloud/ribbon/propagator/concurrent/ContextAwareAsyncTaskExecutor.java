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
import org.springframework.core.task.AsyncTaskExecutor;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Copies current {@link ExecutionContext} to delegate async task executor task.
 *
 * @author Nadim Benabdenbi
 */
public class ContextAwareAsyncTaskExecutor extends ContextAwareExecutor implements AsyncTaskExecutor {
    /**
     * The delegate async task executor.
     */
    private final AsyncTaskExecutor delegate;


    /**
     * Sole Constructor.
     *
     * @param delegate the delegate async task executor..
     */
    public ContextAwareAsyncTaskExecutor(@NotNull AsyncTaskExecutor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute(Runnable task, long startTimeout) {
        delegate.execute(ContextAwareRunnable.wrap(task), startTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Future<?> submit(Runnable task) {
        return delegate.submit(ContextAwareRunnable.wrap(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(ContextAwareCallable.wrap(task));
    }
}
