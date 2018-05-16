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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import java.util.Map;

/**
 * Connection factory adapter that preserves the {@link ExecutionContext} by copying message propagationProperties from/to the current {@link ExecutionContext} pre-filtering property names or entry keys using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@AllArgsConstructor
public class PreservesMessagePropertiesConnectionFactoryAdapter implements ConnectionFactory {
    /**
     * The delegate connection factory.
     */
    private final ConnectionFactory delegate;

    /**
     * The context entry key or message property name filter.
     */
    private final Filter<String> filter;

    /**
     * the extra static entries to copy.
     */
    private Map<String, String> extraStaticEntries;

    /**
     * The message property encoder.
     */
    private final MessagePropertyEncoder encoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public JMSContext createContext() {
        return delegate.createContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMSContext createContext(int sessionMode) {
        return delegate.createContext(sessionMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMSContext createContext(String userName, String password) {
        return delegate.createContext(userName, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMSContext createContext(String userName, String password, int sessionMode) {
        return delegate.createContext(userName, password, sessionMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection() throws JMSException {
        return new PreservesMessagePropertiesConnectionAdapter(delegate.createConnection(), filter, extraStaticEntries, encoder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return new PreservesMessagePropertiesConnectionAdapter(delegate.createConnection(userName, password), filter, extraStaticEntries, encoder);
    }
}
