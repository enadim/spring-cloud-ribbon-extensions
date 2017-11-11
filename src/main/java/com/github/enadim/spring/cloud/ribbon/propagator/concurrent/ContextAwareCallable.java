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

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.switchTo;

/**
 * Copies current {@link ExecutionContext} to delegate callable.
 *
 * @author Nadim Benabdenbi
 */
public class ContextAwareCallable<T> implements Callable<T> {
    /**
     * The delegate callable.
     */
    private final Callable<T> delegate;
    /**
     * the current execution context.
     */
    private final ExecutionContext context;

    /**
     * Sole Constructor: saves the current {@link ExecutionContext} for later {@link #call()} invocation.
     *
     * @param delegate the delegate {@link Callable}
     */
    public ContextAwareCallable(Callable<T> delegate) {
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
     * Wraps a callable to a {@link ContextAwareCallable}
     *
     * @param callable the callable to wrap
     * @param <T>      the callable result type
     * @return The {@link ContextAwareCallable} instance over #callable
     */
    public static <T> ContextAwareCallable<T> wrap(Callable<T> callable) {
        return new ContextAwareCallable<>(callable);
    }

    /**
     * Wraps the a callable's collection to a {@link ContextAwareCallable} collection.
     *
     * @param tasks the callable's collection to wrap
     * @param <T>   the callable result type
     * @return The {@link ContextAwareCallable} instances over #tasks
     */
    public static <T> Collection<Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(ContextAwareCallable::wrap).collect(Collectors.toList());
    }
}
