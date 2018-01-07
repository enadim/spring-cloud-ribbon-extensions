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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecutorServicePropagatorTest extends AbstractExecutionContextAwareExecutorTest {
    private final ExecutorService delegate = mock(ExecutorService.class);
    private final ContextAwareExecutorService mocked = new ContextAwareExecutorService(delegate);
    private final ContextAwareExecutorService propagator = new ContextAwareExecutorService(newSingleThreadExecutor());


    @Test
    public void testShutdown() throws Exception {
        mocked.shutdown();
        verify(delegate).shutdown();
    }

    @Test
    public void testShutdownNow() throws Exception {
        mocked.shutdownNow();
        verify(delegate).shutdownNow();
    }

    @Test
    public void testIsShutdown() throws Exception {
        mocked.isShutdown();
        verify(delegate).isShutdown();
    }

    @Test
    public void testIsTerminated() throws Exception {
        mocked.isTerminated();
        verify(delegate).isTerminated();
    }

    @Test
    public void testAwaitTermination() throws Exception {
        mocked.awaitTermination(1, TimeUnit.MILLISECONDS);
        verify(delegate).awaitTermination(1, TimeUnit.MILLISECONDS);
    }


    @Test
    public void testSubmitCallable() throws Exception {
        current().put(key, value);
        assertThat(propagator.submit(callable).get(), is(value));
    }

    @Test
    public void testSubmitRunnable() throws Exception {
        current().put(key, value);
        propagator.submit(runnable).get();
        assertThat(signal.poll(1, SECONDS), is(value));
    }

    @Test
    public void testSubmitRunnableWithResult() throws Exception {
        current().put(key, value);
        propagator.submit(runnable, true).get();
        assertThat(signal.poll(1, SECONDS), is(value));
    }

    @Test
    public void testInvokeAll() throws Exception {
        current().put(key, value);
        assertThat(propagator.invokeAll(asList(callable, callable))
                .stream()
                .map(AbstractExecutionContextAwareExecutorTest::uncheck)
                .reduce((x, y) -> x + y)
                .get(), is(value + value));
    }

    @Test
    public void testInvokeAllWithTimeOut() throws Exception {
        current().put(key, value);
        assertThat(propagator.invokeAll(asList(callable, callable), 10, SECONDS)
                .stream()
                .map(AbstractExecutionContextAwareExecutorTest::uncheck)
                .reduce((x, y) -> x + y)
                .get(), is(value + value));
    }

    @Test
    public void testInvokeAny() throws Exception {
        current().put(key, value);
        assertThat(propagator.invokeAny(asList(callable, callable)), is(value));
    }

    @Test
    public void testInvokeAnyWithTimeOut() throws Exception {
        current().put(key, value);
        assertThat(propagator.invokeAny(asList(callable, callable), 10, SECONDS), is(value));
    }
}