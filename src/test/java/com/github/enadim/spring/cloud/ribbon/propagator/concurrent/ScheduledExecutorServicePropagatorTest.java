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

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScheduledExecutorServicePropagatorTest extends AbstractExecutionContextAwareExecutorTest {
    private final ContextAwareScheduledExecutorService propagator = new ContextAwareScheduledExecutorService(newScheduledThreadPool(4));

    @Test
    public void scheduleRunnable() throws Exception {
        current().put(key, value);
        propagator.schedule(runnable, 1, MILLISECONDS);
        assertThat(signal.poll(1, SECONDS), is(value));
    }

    @Test
    public void scheduleCallable() throws Exception {
        current().put(key, value);
        propagator.schedule(callable, 1, MILLISECONDS);
        assertThat(signal.poll(1, SECONDS), is(value));
    }

    @Test
    public void scheduleAtFixedRate() throws Exception {
        current().put(key, value);
        propagator.scheduleAtFixedRate(runnable, 1, 1000, MILLISECONDS);
        assertThat(signal.poll(1, SECONDS), is(value));
    }

    @Test
    public void scheduleWithFixedDelay() throws Exception {
        current().put(key, value);
        propagator.scheduleWithFixedDelay(runnable, 1, 1000, MILLISECONDS);
        assertThat(signal.poll(1, SECONDS), is(value));
    }

}