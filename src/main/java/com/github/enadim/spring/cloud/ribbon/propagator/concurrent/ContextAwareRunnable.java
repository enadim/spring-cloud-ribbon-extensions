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

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.switchTo;

/**
 * Copies current {@link ExecutionContext} to delegate runnable.
 *
 * @author Nadim Benabdenbi
 */
public class ContextAwareRunnable implements Runnable {
    /**
     * The delegate runnable.
     */
    private final Runnable delegate;
    /**
     * the current execution context
     */
    private final ExecutionContext context;

    /**
     * Sole constructor: saves the current {@link ExecutionContext} for later {@link #run()} invocation.
     *
     * @param delegate the delegate {@link Runnable}
     */
    public ContextAwareRunnable(Runnable delegate) {
        this.delegate = delegate;
        context = current().copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        switchTo(context);
        delegate.run();
    }

    /**
     * Wraps a Runnable to a {@link ContextAwareRunnable}
     *
     * @param runnable the runnable to wrap
     * @return the instance of {@link ContextAwareRunnable} over the runnable
     */
    public static Runnable wrap(Runnable runnable) {
        return new ContextAwareRunnable(runnable);
    }
}
