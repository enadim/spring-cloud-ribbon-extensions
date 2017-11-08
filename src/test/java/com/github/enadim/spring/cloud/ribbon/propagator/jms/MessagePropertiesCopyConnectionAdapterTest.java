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

import org.junit.Test;

import javax.jms.Connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessagePropertiesCopyConnectionAdapterTest {
    Connection delegate = mock(Connection.class);
    MessagePropertiesCopyConnectionAdapter propagator = new MessagePropertiesCopyConnectionAdapter(delegate, null);

    @Test
    public void createSession() throws Exception {
        assertThat(propagator.createSession(true, 1).getClass(), equalTo(MessagePropertiesCopySessionAdapter.class));
        verify(delegate).createSession(true, 1);
    }

    @Test
    public void getClientID() throws Exception {
        propagator.getClientID();
        verify(delegate).getClientID();
    }

    @Test
    public void setClientID() throws Exception {
        propagator.setClientID(null);
        verify(delegate).setClientID(null);
    }

    @Test
    public void getMetaData() throws Exception {
        propagator.getMetaData();
        verify(delegate).getMetaData();
    }

    @Test
    public void getExceptionListener() throws Exception {
        propagator.getExceptionListener();
        verify(delegate).getExceptionListener();
    }

    @Test
    public void setExceptionListener() throws Exception {
        propagator.setExceptionListener(null);
        verify(delegate).setExceptionListener(null);
    }

    @Test
    public void start() throws Exception {
        propagator.start();
        verify(delegate).start();
    }

    @Test
    public void stop() throws Exception {
        propagator.stop();
        verify(delegate).stop();
    }

    @Test
    public void close() throws Exception {
        propagator.close();
        verify(delegate).close();
    }

    @Test
    public void createConnectionConsumer() throws Exception {
        propagator.createConnectionConsumer(null, null, null, 0);
        verify(delegate).createConnectionConsumer(null, null, null, 0);
    }

    @Test
    public void createDurableConnectionConsumer() throws Exception {
        propagator.createDurableConnectionConsumer(null, null, null, null, 0);
        verify(delegate).createDurableConnectionConsumer(null, null, null, null, 0);
    }
}
