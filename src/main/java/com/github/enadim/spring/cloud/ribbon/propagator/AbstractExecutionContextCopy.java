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
package com.github.enadim.spring.cloud.ribbon.propagator;

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;

/**
 * Abstract {@link ExecutionContext} Copy to a target object.
 * <p>Defines {@link #copy(Object)} method that copies current {@link ExecutionContext} entries pre-filtering using the defined {@link #filter} and the defined copy function {@link #executionContextCopyFunction}.
 *
 * @param <T> The target type of the copy.
 * @author Nadim Benabdenbi
 */
@Slf4j
@Getter
public class AbstractExecutionContextCopy<T> {
    /**
     * The context entry key filter.
     */
    private final Filter<String> filter;
    /**
     * The execution context copy function.
     */
    private final ExecutionContextCopyFunction<T> executionContextCopyFunction;

    /**
     * The extra static entries to copy.
     */
    private final Map<String, String> extraStaticEntries;

    /**
     * Sole Constructor.
     *
     * @param filter                       the context entry key filter
     * @param executionContextCopyFunction the execution context copy function.
     * @param extraStaticEntries           The extra static entries to copy.
     */
    public AbstractExecutionContextCopy(@NotNull Filter<String> filter,
                                        @NotNull ExecutionContextCopyFunction<T> executionContextCopyFunction,
                                        @NotNull Map<String, String> extraStaticEntries) {
        this.filter = filter;
        this.executionContextCopyFunction = executionContextCopyFunction;
        this.extraStaticEntries = extraStaticEntries;
    }

    /**
     * Copies current {@link ExecutionContext} attributes keys that matches {@link #filter} using the {@link #executionContextCopyFunction}.
     *
     * @param t the target type of the copy.
     * @return the copied {@link ExecutionContext} entries.
     */
    protected List<Map.Entry<String, String>> copy(T t) {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        copy(t, current().entrySet(), result);
        copy(t, extraStaticEntries.entrySet(), result);
        return result;
    }

    /**
     * Copies the entry set that matches {@link #filter} using the {@link #executionContextCopyFunction}.
     *
     * @param t        the target type of the copy.
     * @param entrySet the entry set to copy
     * @param result   the copied entries
     */
    private void copy(T t, Set<Entry<String, String>> entrySet, List<Map.Entry<String, String>> result) {
        entrySet.stream()
                .filter(x -> filter.accept(x.getKey()))
                .forEach(x -> {
                    try {
                        executionContextCopyFunction.copy(t, x.getKey(), x.getValue());
                        result.add(x);
                    } catch (Exception e) {
                        log.debug("Failed to copy {}.", x, e);
                    }
                });
    }

    /**
     * The function that copies the execution context attributes to a target object.
     *
     * @param <T> the target type of the copy.
     */
    @FunctionalInterface
    public interface ExecutionContextCopyFunction<T> {

        /**
         * Copies the execution context attributes to the target object.
         *
         * @param t     the target.
         * @param key   the attribute key to copy.
         * @param value the attribute value to copy.
         * @throws Exception the checked sub class exception
         */
        void copy(T t, String key, String value) throws Exception;
    }
}
