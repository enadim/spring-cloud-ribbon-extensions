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

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ExecutionContextAwareExecutorTest extends AbstractExecutionContextAwareExecutorTest {

    private final ExecutionContextAwareExecutor propagator = new ExecutionContextAwareExecutor(newSingleThreadExecutor());
    protected final String key = "key";
    protected final String value = "value";
    protected final AtomicBoolean holder = new AtomicBoolean();
    protected final Runnable runnable = () -> holder.set(current().containsKey(key));


    @Test
    public void testExecute() throws Exception {
        current().put(key, value);
        propagator.execute(runnable);
        Thread.sleep(1000);
        assertThat(holder.get(), is(true));
    }
}