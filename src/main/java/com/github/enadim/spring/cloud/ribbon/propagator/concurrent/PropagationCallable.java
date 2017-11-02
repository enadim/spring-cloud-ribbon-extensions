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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.switchTo;

/**
 * {@link RibbonRuleContext} Propagator for {@link Callable}.
 *
 * @author Nadim Benabdenbi
 */
public class PropagationCallable<T> implements Callable<T> {
    /**
     * the delegate {@link Callable}
     */
    private final Callable<T> delegate;
    /**
     * the current {@link RibbonRuleContext}
     */
    private final RibbonRuleContext context;

    /**
     * Sole Constructor: saves the current {@link RibbonRuleContext} for later {@link #call()} invocation.
     *
     * @param delegate the delegate {@link Callable}
     */
    public PropagationCallable(Callable<T> delegate) {
        this.delegate = delegate;
        context = current().copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T call() throws Exception {
        switchTo(context);
        return delegate.call();
    }

    /**
     * Wraps a callable to a {@link PropagationCallable}
     *
     * @param callable the callable to wrap
     * @param <T>      the callable result type
     * @return The {@link PropagationCallable} instance over #callable
     */
    public static <T> PropagationCallable<T> wrap(Callable<T> callable) {
        return new PropagationCallable<>(callable);
    }

    /**
     * Wraps the a callable's collection to a {@link PropagationCallable} collection.
     *
     * @param tasks the callable's collection to wrap
     * @param <T>   the callable result type
     * @return The {@link PropagationCallable} instances over #tasks
     */
    public static <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(PropagationCallable::wrap).collect(Collectors.toList());
    }
}
