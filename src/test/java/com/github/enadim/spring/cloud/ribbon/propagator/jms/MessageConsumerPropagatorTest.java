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

import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageConsumerPropagatorTest {
    MessageConsumer delegate = mock(MessageConsumer.class);
    MessageConsumerPropagator propagator = new MessageConsumerPropagator(delegate, null);

    @Test
    public void getMessageSelector() throws Exception {
        propagator.getMessageSelector();
        verify(delegate).getMessageSelector();
    }

    @Test
    public void getMessageListener() throws Exception {
        propagator.getMessageListener();
        verify(delegate).getMessageListener();
    }

    @Test
    public void setMessageListener() throws Exception {
        propagator.setMessageListener(null);
        verify(delegate).setMessageListener(any(MessageListenerPropagator.class));
    }

    @Test
    public void receive() throws Exception {
        when(delegate.getMessageListener()).thenReturn(mock(MessageListenerPropagator.class));
        propagator.receive();
        verify(delegate).receive();
        reset(delegate);
        when(delegate.getMessageListener()).thenReturn(mock(MessageListener.class));
        propagator.receive();
        verify(delegate).receive();
    }

    @Test
    public void receive1() throws Exception {
        when(delegate.getMessageListener()).thenReturn(mock(MessageListenerPropagator.class));
        propagator.receive(0);
        verify(delegate).receive(0);
        reset(delegate);
        when(delegate.getMessageListener()).thenReturn(mock(MessageListener.class));
        propagator.receive(0);
        verify(delegate).receive(0);
    }

    @Test
    public void receiveNoWait() throws Exception {
        when(delegate.getMessageListener()).thenReturn(mock(MessageListenerPropagator.class));
        propagator.receiveNoWait();
        verify(delegate).receiveNoWait();
        reset(delegate);
        when(delegate.getMessageListener()).thenReturn(mock(MessageListener.class));
        propagator.receiveNoWait();
        verify(delegate).receiveNoWait();
    }

    @Test
    public void close() throws Exception {
        propagator.close();
        verify(delegate).close();
    }
}
