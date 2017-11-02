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

import com.github.enadim.spring.cloud.ribbon.propagator.AbstractAttributesPropagator.PropagationFunction;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

public class AbstractAttributesPropagatorTest {
    private Set<String> attributes = new HashSet<>(asList("1", "2"));
    private Map<String, String> collector = new HashMap<>();

    @Test
    public void test_getters() {
        PropagationFunction<Map<String, String>> function = Map::put;
        @SuppressWarnings("unchecked")
        AbstractAttributesPropagator<Map<String, String>>
                propagator
                = (AbstractAttributesPropagator<Map<String, String>>)
                mock(AbstractAttributesPropagator.class, withSettings()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .useConstructor(attributes, function));
        assertThat(propagator.getKeysToPropagate(), is(attributes));
        assertThat(propagator.getPropagationFunction(), is(function));

    }

    @Test
    public void test_propagate() {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        PropagationFunction<Map<String, String>> function = Map::put;
        @SuppressWarnings("unchecked")
        AbstractAttributesPropagator<Map<String, String>> propagator = (AbstractAttributesPropagator<Map<String, String>>)
                mock(AbstractAttributesPropagator.class, withSettings()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .useConstructor(attributes, function));
        propagator.propagate(collector);
        assertThat(collector.get("1"), is("1"));
        assertThat(collector.get("2"), is("2"));
        assertThat(collector.containsKey("3"), is(false));
    }

    @Test
    public void test_propagate_with_exception() {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        PropagationFunction<Map<String, String>> function = (x, y, z) -> {
            if ("1".equals(y)) {
                throw new IllegalArgumentException(y);
            }
            x.put(y, z);
        };
        @SuppressWarnings("unchecked")
        AbstractAttributesPropagator<Map<String, String>> propagator = (AbstractAttributesPropagator<Map<String, String>>)
                mock(AbstractAttributesPropagator.class, withSettings()
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .useConstructor(attributes, function));
        propagator.propagate(collector);
        assertThat(collector.containsKey("1"), is(false));
        assertThat(collector.get("2"), is("2"));
        assertThat(collector.containsKey("3"), is(false));
    }
}