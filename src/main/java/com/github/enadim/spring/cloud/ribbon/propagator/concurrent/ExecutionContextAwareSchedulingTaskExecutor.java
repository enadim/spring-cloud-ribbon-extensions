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
import org.springframework.scheduling.SchedulingTaskExecutor;

import javax.validation.constraints.NotNull;

/**
 * Copies current {@link ExecutionContext} to delegate scheduled executor task.
 *
 * @author Nadim Benabdenbi
 */
public class ExecutionContextAwareSchedulingTaskExecutor extends ExecutionContextAwareAsyncTaskExecutor implements SchedulingTaskExecutor {
    /**
     * The delegate scheduling task executor.
     */
    private final SchedulingTaskExecutor delegate;


    /**
     * Sole Constructor
     *
     * @param delegate the delegate scheduling task executor.
     */
    public ExecutionContextAwareSchedulingTaskExecutor(@NotNull SchedulingTaskExecutor delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean prefersShortLivedTasks() {
        return delegate.prefersShortLivedTasks();
    }
}
