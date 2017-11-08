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
import com.github.enadim.spring.cloud.ribbon.propagator.AbstractExecutionContextCopy;
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import lombok.extern.slf4j.Slf4j;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Message producer adapter that copies the current {@link ExecutionContext} to the message properties pre-filtering entry keys using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class MessagePropertiesCopyMessageProducerAdapter extends AbstractExecutionContextCopy<Message> implements MessageProducer {
    /**
     * The context entry key filter.
     */
    private final MessageProducer delegate;

    /**
     * Sole constructor.
     *
     * @param delegate        the delegate message producer.
     * @param keysToPropagate the context entry key filter.
     */
    public MessagePropertiesCopyMessageProducerAdapter(@NotNull MessageProducer delegate, @NotNull Filter<String> keysToPropagate) {
        super(keysToPropagate, (x, y, z) -> x.setStringProperty(y, z));
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableMessageID(boolean value) throws JMSException {
        delegate.setDisableMessageID(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisableMessageID() throws JMSException {
        return delegate.getDisableMessageID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        delegate.setDisableMessageTimestamp(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        return delegate.getDisableMessageTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeliveryMode(int deliveryMode) throws JMSException {
        delegate.setDeliveryMode(deliveryMode);
    }

    @Override
    public int getDeliveryMode() throws JMSException {
        return delegate.getDeliveryMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPriority(int defaultPriority) throws JMSException {
        delegate.setPriority(defaultPriority);
    }

    @Override
    public int getPriority() throws JMSException {
        return delegate.getPriority();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeToLive(long timeToLive) throws JMSException {
        delegate.setTimeToLive(timeToLive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeToLive() throws JMSException {
        return delegate.getTimeToLive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Destination getDestination() throws JMSException {
        return delegate.getDestination();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws JMSException {
        delegate.close();
    }

    /**
     * Trace copied entries.
     *
     * @param copiedEntries the copied entries.
     */
    private void trace(List<Map.Entry<String, String>> copiedEntries) {
        log.trace("Context entries copied to message properties {}", copiedEntries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Message message) throws JMSException {
        trace(copy(message));
        delegate.send(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        trace(copy(message));
        delegate.send(message, deliveryMode, priority, timeToLive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Destination destination, Message message) throws JMSException {
        trace(copy(message));
        delegate.send(destination, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        trace(copy(message));
        delegate.send(destination, message, deliveryMode, priority, timeToLive);
    }
}
