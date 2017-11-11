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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.validation.constraints.NotNull;

/**
 * Message consumer adapter that copies message propagationProperties to the current {@link ExecutionContext} pre-filtering property names using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class PreservesMessagePropertiesMessageConsumerAdapter extends AbstractPreservesMessageProperties implements MessageConsumer {
    /**
     * the delegate {@link MessageListener}
     */
    private final MessageConsumer delegate;

    /**
     * Sole constructor.
     *
     * @param delegate the delegate message consumer
     * @param filter   the message property name filter
     */
    public PreservesMessagePropertiesMessageConsumerAdapter(@NotNull MessageConsumer delegate, @NotNull Filter<String> filter) {
        super(filter);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageSelector() throws JMSException {
        return delegate.getMessageSelector();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageListener getMessageListener() throws JMSException {
        return delegate.getMessageListener();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessageListener(MessageListener listener) throws JMSException {
        delegate.setMessageListener(new PreservesMessagePropertiesMessageListener(listener, getFilter()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receive() throws JMSException {
        Message message = delegate.receive();
        if (!(delegate.getMessageListener() instanceof PreservesMessagePropertiesMessageListener)) {
            copyFromMessage(message);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receive(long timeout) throws JMSException {
        Message message = delegate.receive(timeout);
        if (!(delegate.getMessageListener() instanceof PreservesMessagePropertiesMessageListener)) {
            copyFromMessage(message);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receiveNoWait() throws JMSException {
        Message message = delegate.receiveNoWait();
        if (!(delegate.getMessageListener() instanceof PreservesMessagePropertiesMessageListener)) {
            copyFromMessage(message);
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws JMSException {
        delegate.close();
    }
}
