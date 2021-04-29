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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class ExecutionContextAwareExecutorTest extends AbstractExecutionContextAwareExecutorTest {

    private final ContextAwareExecutor propagator = new ContextAwareExecutor(newSingleThreadExecutor());

    @Test
    public void testExecute() throws Exception {
        current().put(key, value);
        propagator.execute(runnable);
        Assertions.assertThat(signal.poll(5, TimeUnit.SECONDS)).isEqualTo(value);
    }
}