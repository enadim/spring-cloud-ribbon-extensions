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
package com.github.enadim.spring.cloud.ribbon.api;

import java.io.Serializable;
import java.util.Map;

/**
 * Ribbon discovery filter context, stores the attributes based on which the server matching will be performed.
 *
 * @author Nadim Benabdenbi
 */
public interface RibbonRuleContext extends Serializable {

    /**
     * put the context attribute.
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return the context instance
     */
    RibbonRuleContext put(String key, String value);

    /**
     * put the context attribute if absent (is not guaranteed unless enabling concurrency) .
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return the context instance
     */
    RibbonRuleContext putIfAbsent(String key, String value);

    /**
     * Check if an attribute key is present.
     *
     * @param key the attribute key
     * @return true when the attribute key is present otherwise false.
     */
    boolean containsKey(String key);

    /**
     * Retrieves the context attribute value.
     *
     * @param key the attribute key
     * @return the attribute value
     */
    String get(String key);

    /**
     * Removes the context attribute.
     *
     * @param key the context attribute
     * @return the context instance
     */
    RibbonRuleContext remove(String key);

    /**
     * Enables concurrent access
     *
     * @return the context instance
     */
    RibbonRuleContext enableConcurrency();


    /**
     * Retrieves the attributes.
     *
     * @return the stored attributes as an unmodifiable {@link Map}
     */
    Map<String, String> getAttributes();

    /**
     * Copies the current instances.
     *
     * @return a context copy
     */
    DefaultRibbonRuleContext copy();


}
