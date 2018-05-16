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
package com.github.enadim.spring.cloud.ribbon.propagator.jms;

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.Collections.list;

/**
 * Copies message propagationProperties to the current {@link ExecutionContext} pre-filtering property names using the provided {@link #filter}.
 * <p>Defines the utility method {@link #copyFromMessage(Message)} to be used by implementation
 */
@Slf4j
@Getter
@AllArgsConstructor
public abstract class AbstractPreservesMessageProperties {
    /**
     * The message property name filter.
     */
    private final Filter<String> filter;

    /**
     * The message property encoder.
     */
    private final MessagePropertyEncoder encoder;

    /**
     * Copies message propagationProperties to the current {@link ExecutionContext}.
     *
     * @param message the message to process.
     * @return the same message after process
     */
    protected Message copyFromMessage(Message message) {
        if (message != null) {
            try {
                ExecutionContext context = current();
                List<String> eligiblePropertyNames = new ArrayList<>();
                list((Enumeration<String>) message.getPropertyNames()).stream()
                        .forEach(x -> copy(context, message, x, eligiblePropertyNames));
                log.trace("Message Properties copied {}", eligiblePropertyNames);
            } catch (JMSException e) {
                log.debug("Failed to copy message properties", e);
            }
        }
        return message;
    }

    /**
     * Copies the message property to the current execution context. failing silently when an exception is thrown.
     *
     * @param context      the ribbon context
     * @param message      the jms message
     * @param propertyName the property name to copy
     * @param collected    the propagationProperties that have been copied
     */
    private void copy(ExecutionContext context, Message message, String propertyName, List<String> collected) {
        try {
            String decoded = encoder.decode(propertyName);
            if (filter.accept(decoded)) {
                String value = message.getStringProperty(propertyName);
                context.put(decoded, value);
                collected.add(decoded);
            }
        } catch (JMSException e) {
            log.debug("Failed to copy message property [{}]", propertyName);
        }
    }
}
