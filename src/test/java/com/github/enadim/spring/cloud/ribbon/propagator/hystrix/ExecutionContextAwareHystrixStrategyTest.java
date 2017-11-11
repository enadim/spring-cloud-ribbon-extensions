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
package com.github.enadim.spring.cloud.ribbon.propagator.hystrix;

import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareCallable;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecutionContextAwareHystrixStrategyTest {
    HystrixConcurrencyStrategy delegate = mock(HystrixConcurrencyStrategy.class);
    ExecutionContextAwareHystrixStrategy strategy = new ExecutionContextAwareHystrixStrategy(delegate);

    @Test
    public void getBlockingQueue() throws Exception {
        strategy.getBlockingQueue(5);
        verify(delegate).getBlockingQueue(5);
    }

    @Test
    public void getRequestVariable() throws Exception {
        strategy.getRequestVariable(null);
        verify(delegate).getRequestVariable(null);
    }

    @Test
    public void getThreadPool() throws Exception {
        strategy.getThreadPool(null, null);
        verify(delegate).getThreadPool(null, null);
    }

    @Test
    public void getThreadPool1() throws Exception {
        strategy.getThreadPool(null, null, null, null, null, null);
        verify(delegate).getThreadPool(null, null, null, null, null, null);
    }

    @Test
    public void wrapCallable() throws Exception {
        MatcherAssert.assertThat(strategy.wrapCallable(null).getClass(), Matchers.equalTo(ContextAwareCallable.class));
    }
}