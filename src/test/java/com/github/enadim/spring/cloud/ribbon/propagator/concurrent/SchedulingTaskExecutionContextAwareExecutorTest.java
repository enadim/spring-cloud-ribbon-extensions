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
import org.springframework.scheduling.SchedulingTaskExecutor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SchedulingTaskExecutionContextAwareExecutorTest extends AbstractExecutionContextAwareExecutorTest {
    private final SchedulingTaskExecutor delegate = mock(SchedulingTaskExecutor.class);
    private final ContextAwareSchedulingTaskExecutor propagator = new ContextAwareSchedulingTaskExecutor(delegate);

    @Test
    public void prefersShortLivedTasks() throws Exception {
        propagator.prefersShortLivedTasks();
        verify(delegate).prefersShortLivedTasks();
    }
}