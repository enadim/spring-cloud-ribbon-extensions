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

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Collections.unmodifiableMap;

/**
 * The default ribbon rule context.
 * <p>Designed to store attributes that will be used by the ribbon rule predicates.
 *
 * @author Nadim Benabdenbi
 */
public class DefaultRibbonRuleContext implements RibbonRuleContext {
    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The context attributes.
     */
    private Map<String, String> attributes;

    /**
     * Constructs a new context with an empty attributes.
     */
    public DefaultRibbonRuleContext() {
        attributes = new HashMap<>();
    }

    /**
     * Constructs a new context with the given attributes.
     *
     * @param attributes the attributes to starts with.
     */
    public DefaultRibbonRuleContext(@NotNull Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RibbonRuleContext put(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RibbonRuleContext putIfAbsent(String key, String value) {
        attributes.putIfAbsent(key, value);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return attributes.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */

    @Override

    public String get(String key) {
        return attributes.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RibbonRuleContext remove(String key) {
        attributes.remove(key);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RibbonRuleContext enableConcurrency() {
        if (!(attributes instanceof ConcurrentMap)) {
            attributes = new ConcurrentHashMap<>(attributes);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAttributes() {
        return unmodifiableMap(attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRibbonRuleContext copy() {
        return new DefaultRibbonRuleContext(attributes);
    }
}
