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
import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.validation.constraints.NotNull;

/**
 * Message listener adapter that copies message properties to the current {@link ExecutionContext} pre-filtering property names using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class MessagePropertiesCopyMessageListener extends AbstractMessagePropertiesCopy implements MessageListener {
    /**
     * the delegate {@link MessageListener}
     */
    private final MessageListener delegate;

    /**
     * Sole constructor.
     *
     * @param delegate the delegate {@link MessageListener}
     * @param filter   the message property name filter
     */
    public MessagePropertiesCopyMessageListener(@NotNull MessageListener delegate, @NotNull Filter<String> filter) {
        super(filter);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        delegate.onMessage(copyFromMessage(message));
    }
}
