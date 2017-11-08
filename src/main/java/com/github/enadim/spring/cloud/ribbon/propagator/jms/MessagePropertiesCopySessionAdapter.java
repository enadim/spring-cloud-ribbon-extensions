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

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

/**
 * Session adapter that preserves the {@link ExecutionContext} by copying message properties from/to the current {@link ExecutionContext} entry pre-filtering property names or entry keys using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class MessagePropertiesCopySessionAdapter extends AbstractExecutionContextCopy<Message>
        implements Session {
    /**
     * the delegate session
     */
    private final Session delegate;

    /**
     * @param delegate the delegate session
     * @param filter   the context entry key filter
     */
    public MessagePropertiesCopySessionAdapter(Session delegate, Filter<String> filter) {
        super(filter, (x, y, z) -> x.setStringProperty(y, z));
        this.delegate = delegate;
    }

    /**
     * copies the current execution context to message properties.
     *
     * @param message the target message.
     * @param <T>     the type of the message
     * @return the message
     * @throws JMSException thrown by the delegate
     */
    private <T extends Message> T copyExecutionContextToMessageProperties(T message) throws JMSException {
        List<Entry<String, String>> entries = copy(message);
        log.trace("propagated {}", entries);
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createBytesMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapMessage createMapMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createMapMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message createMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createObjectMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createObjectMessage(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createStreamMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextMessage createTextMessage() throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createTextMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextMessage createTextMessage(String text) throws JMSException {
        return copyExecutionContextToMessageProperties(delegate.createTextMessage(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTransacted() throws JMSException {
        return delegate.getTransacted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAcknowledgeMode() throws JMSException {
        return delegate.getAcknowledgeMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws JMSException {
        delegate.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws JMSException {
        delegate.rollback();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws JMSException {
        delegate.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recover() throws JMSException {
        delegate.recover();
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
        delegate.setMessageListener(listener instanceof MessagePropertiesCopyMessageListener ? listener : new MessagePropertiesCopyMessageListener(listener, getFilter()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        delegate.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        return new MessagePropertiesCopyMessageProducerAdapter(delegate.createProducer(destination), getFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new MessagePropertiesCopyMessageConsumerAdapter(delegate.createConsumer(destination), getFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return new MessagePropertiesCopyMessageConsumerAdapter(delegate.createConsumer(destination, messageSelector), getFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException {
        return new MessagePropertiesCopyMessageConsumerAdapter(delegate.createConsumer(destination, messageSelector, noLocal), getFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queue createQueue(String queueName) throws JMSException {
        return delegate.createQueue(queueName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Topic createTopic(String topicName) throws JMSException {
        return delegate.createTopic(topicName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return delegate.createDurableSubscriber(topic, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        return delegate.createDurableSubscriber(topic, name, messageSelector, noLocal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return delegate.createBrowser(queue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        return delegate.createBrowser(queue, messageSelector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return delegate.createTemporaryQueue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return delegate.createTemporaryTopic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(String name) throws JMSException {
        delegate.unsubscribe(name);
    }
}
