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

import org.junit.jupiter.api.Test;
import org.springframework.core.task.AsyncListenableTaskExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AsyncListenableTaskExecutionContextAwareExecutorTest extends AbstractExecutionContextAwareExecutorTest {
    private final AsyncListenableTaskExecutor delegate = mock(AsyncListenableTaskExecutor.class);
    private final ContextAwareAsyncListenableTaskExecutor propagator = new ContextAwareAsyncListenableTaskExecutor(delegate);

    @Test
    public void submitRunnable() throws Exception {
        propagator.submitListenable(runnable);
        verify(delegate).submitListenable(any(ContextAwareRunnable.class));
    }

    @Test
    public void submitCallable() throws Exception {
        propagator.submitListenable(callable);
        verify(delegate).submitListenable(any(ContextAwareCallable.class));
    }
}