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

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Connection adapter that preserves the {@link ExecutionContext} by copying message propagationProperties from/to the current {@link ExecutionContext} entry pre-filtering property names or entry keys using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
public class PreservesMessagePropertiesConnectionAdapter implements Connection {
    /**
     * The delegate connection.
     */
    private final Connection delegate;
    /**
     * The context entry key or message property name filter.
     */
    private final Filter<String> filter;
    /**
     * the extra static entries to copy.
     */
    private Map<String, String> extraStaticEntries = new HashMap<>();

    /**
     * Sole constructor.
     *
     * @param delegate           the delegate connection factory.
     * @param filter             the keys to copy.
     * @param extraStaticEntries The extra static entries to copy.
     */
    public PreservesMessagePropertiesConnectionAdapter(@NotNull Connection delegate,
                                                       @NotNull Filter<String> filter,
                                                       @NotNull Map<String, String> extraStaticEntries) {
        this.delegate = delegate;
        this.filter = filter;
        this.extraStaticEntries = extraStaticEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return new PreservesMessagePropertiesSessionAdapter(delegate.createSession(transacted, acknowledgeMode), filter, extraStaticEntries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientID() throws JMSException {
        return delegate.getClientID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientID(String clientID) throws JMSException {
        delegate.setClientID(clientID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return delegate.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return delegate.getExceptionListener();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        delegate.setExceptionListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws JMSException {
        delegate.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws JMSException {
        delegate.stop();
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
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return delegate.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return delegate.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }
}
