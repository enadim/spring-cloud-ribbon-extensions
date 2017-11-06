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
import org.springframework.core.task.AsyncTaskExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AsyncTaskExecutorPropagatorTest extends AbstractExecutorPropagatorTest {
    private final AsyncTaskExecutor delegate = mock(AsyncTaskExecutor.class);
    private final AsyncTaskExecutorPropagator propagator = new AsyncTaskExecutorPropagator(delegate);

    @Test
    public void execute() throws Exception {
        propagator.execute(runnable, 0);
        verify(delegate).execute(any(PropagationRunnable.class), anyLong());
    }

    @Test
    public void submitRunnable() throws Exception {
        propagator.submit(runnable);
        verify(delegate).submit(any(PropagationRunnable.class));
    }

    @Test
    public void submitCallable() throws Exception {
        propagator.submit(callable);
        verify(delegate).submit(any(PropagationCallable.class));
    }
}