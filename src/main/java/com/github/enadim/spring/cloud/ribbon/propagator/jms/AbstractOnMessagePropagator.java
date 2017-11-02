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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static java.util.Collections.list;

/**
 * Jms Propagation on receiving message.
 * <p> Exposes the utility method {@link #processMessage(Message)} to be used by implementation
 */
@Slf4j
@Getter
public abstract class AbstractOnMessagePropagator {
    /**
     * the attribute keys to propagate
     */
    private final Set<String> keysToPropagate;

    /**
     * Sole constructor.
     *
     * @param keysToPropagate the attribute keys to propagate
     */
    public AbstractOnMessagePropagator(@NotNull Set<String> keysToPropagate) {
        this.keysToPropagate = keysToPropagate;
    }

    /**
     * Copies jms message properties to the current context.
     *
     * @param message the message to process.
     * @return the same message after process
     */
    @SuppressWarnings("unchecked")
    protected Message processMessage(Message message) {
        if (message != null) {
            try {
                RibbonRuleContext context = current();
                List<String> collected = new ArrayList<>();
                list((Enumeration<String>) message.getPropertyNames()).stream()
                        .filter(keysToPropagate::contains)
                        .forEach(x -> put(context, message, x, collected));
                log.trace("Propagated {}", collected);
            } catch (JMSException e) {
                log.debug("Failed to copy jms properties", e);
            }
        }
        return message;
    }

    /**
     * copies the jms property from the message to the context. failing silently when an exception is thrown.
     *
     * @param context   the ribbon context
     * @param message   the jms message
     * @param property  the property to copy
     * @param collected the properties that have been copied
     */
    private void put(RibbonRuleContext context, Message message, String property, List<String> collected) {
        try {
            context.put(property, message.getStringProperty(property));
            collected.add(property);
        } catch (Exception e) {
            log.debug("Failed to copy jms property [{}]", property);
        }
    }
}
