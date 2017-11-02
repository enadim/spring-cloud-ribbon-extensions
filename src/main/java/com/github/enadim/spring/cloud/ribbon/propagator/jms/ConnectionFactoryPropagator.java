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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Set;

/**
 * {@link RibbonRuleContext} Propagator for {@link ConnectionFactory}.
 *
 * @author Nadim Benabdenbi
 */
public class ConnectionFactoryPropagator implements ConnectionFactory {
    /**
     * the delegate connection factory
     */
    private final ConnectionFactory delegate;
    /**
     * the keys to propagate
     */
    private final Set<String> keysToPropagate;

    /**
     * Sole constructor
     *
     * @param delegate        the delegate connection factory
     * @param keysToPropagate the keys to propagate
     */
    public ConnectionFactoryPropagator(ConnectionFactory delegate, Set<String> keysToPropagate) {
        this.delegate = delegate;
        this.keysToPropagate = keysToPropagate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection() throws JMSException {
        return new ConnectionPropagator(delegate.createConnection(), keysToPropagate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return new ConnectionPropagator(delegate.createConnection(userName, password), keysToPropagate);
    }
}
