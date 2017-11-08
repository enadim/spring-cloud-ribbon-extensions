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
package com.github.enadim.spring.cloud.ribbon.context;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Collections.unmodifiableMap;

/**
 * Execution context that is based on {@link HashMap} which can be converted to a {@link ConcurrentHashMap} when concurrent access is needed.
 *
 * @author Nadim Benabdenbi
 */
public class DefaultExecutionContext implements ExecutionContext {
    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The context entries.
     */
    private Map<String, String> entries;

    /**
     * Constructs a new context with an empty entries.
     */
    public DefaultExecutionContext() {
        entries = new HashMap<>();
    }

    /**
     * Constructs a new context with the given entries.
     *
     * @param entries the entries to starts with.
     */
    public DefaultExecutionContext(@NotNull Map<String, String> entries) {
        this.entries = new HashMap<>(entries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContext put(String key, String value) {
        entries.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContext putIfAbsent(String key, String value) {
        entries.putIfAbsent(key, value);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */

    @Override

    public String get(String key) {
        return entries.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContext remove(String key) {
        entries.remove(key);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContext enableConcurrency() {
        if (!(entries instanceof ConcurrentMap)) {
            entries = new ConcurrentHashMap<>(entries);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        return unmodifiableMap(entries).entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultExecutionContext copy() {
        return new DefaultExecutionContext(entries);
    }
}
