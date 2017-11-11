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

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Execution context that stores entries to be used later in the process.
 * <p><strong>WARNING</strong>: This context should never be used to register business logic: Code Reviewers should forbid such behaviors.
 * It is true that storing such logic in the context will reduce drastically the coding effort however this is very bad choice.
 *
 * @author Nadim Benabdenbi
 */
public interface ExecutionContext extends Serializable {

    /**
     * Associates the specified value with the specified key in this context.
     * If the context previously contained a entry for the key, the old value is replaced by the specified value.
     * stores the context entry.
     *
     * @param key   the entry key
     * @param value the entry value
     * @return the context instance
     */
    ExecutionContext put(String key, String value);

    /**
     * If the specified key is not already associated with a value (or is mapped to null) associates it with the given value.
     *
     * @param key   the entry key
     * @param value the entry value
     * @return {@code this}
     */
    ExecutionContext putIfAbsent(String key, String value);

    /**
     * Check if an entry key is present.
     *
     * @param key the entry key
     * @return {@code true} when the entry key is present otherwise {@code false}.
     */
    boolean containsKey(String key);

    /**
     * Retrieves the entry value matching the given key.
     *
     * @param key the entry key
     * @return the entry value
     */
    String get(String key);

    /**
     * Removes the context entry.
     *
     * @param key the entry key
     * @return {@code this}
     */
    ExecutionContext remove(String key);

    /**
     * Enables concurrent access.
     *
     * @return {@code this}
     */
    ExecutionContext enableConcurrency();


    /**
     * Retrieves the entry set.
     *
     * @return the stored entries.
     */
    Set<Entry<String, String>> entrySet();

    /**
     * Copies the current instance.
     *
     * @return a deep copy of {@code this} instance.
     */
    ExecutionContext copy();
}
