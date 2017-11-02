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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.PropagationCallable;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.enadim.spring.cloud.ribbon.propagator.concurrent.PropagationCallable.wrap;

/**
 * Propagate the {@link RibbonRuleContext} to {@link Hystrix} commands: see <a href="https://github.com/Netflix/Hystrix/wiki/Plugins#concurrency-strategy)">Histrix Wiki</a>..
 *
 * @see PropagationCallable
 */
public class HystrixPropagationStrategy extends HystrixConcurrencyStrategy {
    /**
     * the delegate {@link HystrixConcurrencyStrategy}
     */
    private HystrixConcurrencyStrategy delegate;

    /**
     * Sole Constructor
     *
     * @param delegate the delegate {@link HystrixConcurrencyStrategy}
     */
    public HystrixPropagationStrategy(HystrixConcurrencyStrategy delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return delegate.getBlockingQueue(maxQueueSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return delegate.getRequestVariable(rv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
        return delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return wrap(delegate.wrapCallable(callable));
    }
}
