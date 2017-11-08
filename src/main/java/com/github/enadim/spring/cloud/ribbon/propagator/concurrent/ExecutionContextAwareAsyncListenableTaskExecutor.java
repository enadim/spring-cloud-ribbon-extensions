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
import org.springframework.util.concurrent.ListenableFuture;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;

/**
 * Copies current {@link ExecutionContext} to delegate async listenable executor task.
 *
 * @author Nadim Benabdenbi
 */
public class ExecutionContextAwareAsyncListenableTaskExecutor extends ExecutionContextAwareAsyncTaskExecutor implements AsyncListenableTaskExecutor {
    /**
     * the delegate async listenable task executor.
     */
    private final AsyncListenableTaskExecutor delegate;

    /**
     * Sole Constructor
     *
     * @param delegate the delegate executor service.
     */
    public ExecutionContextAwareAsyncListenableTaskExecutor(@NotNull AsyncListenableTaskExecutor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return delegate.submitListenable(ExecutionContextAwareRunnable.wrap(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return delegate.submitListenable(ExecutionContextAwareCallable.wrap(task));
    }
}
