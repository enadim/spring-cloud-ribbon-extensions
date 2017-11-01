/**
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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;

/**
 * {@link RibbonRuleContext} Feign Propagator.
 * <p>Copies current {@link RibbonRuleContext} attributes in {@link #attributesToPropagate} using the {@link #propagationFunction}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class AbstractAttributesPropagator<T> {
    private final Set<String> attributesToPropagate;
    private final PropagationFunction<T> propagationFunction;

    public AbstractAttributesPropagator(@NotNull Set<String> attributesToPropagate, @NotNull PropagationFunction<T> propagationFunction) {
        this.attributesToPropagate = attributesToPropagate;
        this.propagationFunction = propagationFunction;
    }

    public List<Map.Entry<String, String>> propagate(T t) {
        List<Map.Entry<String, String>> result = new ArrayList<>(attributesToPropagate.size());
        current().getAttributes()
                .entrySet()
                .stream()
                .filter(x -> attributesToPropagate.contains(x.getKey()))
                .forEach(x -> {
                    try {
                        propagationFunction.propagate(t, x.getKey(), x.getValue());
                        result.add(x);
                    } catch (Exception e) {
                        log.warn("Failed to propagate {}.", x, e);
                    }
                });
        return result;
    }

    /**
     * Propagation Function
     */
    public interface PropagationFunction<T> {
        void propagate(T t, String key, String value) throws Exception;
    }
}
