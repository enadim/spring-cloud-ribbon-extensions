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

import javax.jms.Message;
import javax.jms.MessageProducer;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessagePropertiesCopyMessageProducerAdapterTest {
    Set<String> keys = new HashSet<>(asList("1"));
    MessageProducer delegate = mock(MessageProducer.class);
    MessagePropertiesCopyMessageProducerAdapter propagator = new MessagePropertiesCopyMessageProducerAdapter(delegate, keys::contains);
    Message message = mock(Message.class);

    @Test
    public void setDisableMessageID() throws Exception {
        propagator.setDisableMessageID(true);
        verify(delegate).setDisableMessageID(true);
    }

    @Test
    public void getDisableMessageID() throws Exception {
        propagator.getDisableMessageID();
        verify(delegate).getDisableMessageID();
    }

    @Test
    public void setDisableMessageTimestamp() throws Exception {
        propagator.setDisableMessageTimestamp(true);
        verify(delegate).setDisableMessageTimestamp(true);
    }

    @Test
    public void getDisableMessageTimestamp() throws Exception {
        propagator.getDisableMessageTimestamp();
        verify(delegate).getDisableMessageTimestamp();
    }

    @Test
    public void setDeliveryMode() throws Exception {
        propagator.setDeliveryMode(1);
        verify(delegate).setDeliveryMode(1);
    }

    @Test
    public void getDeliveryMode() throws Exception {
        propagator.getDeliveryMode();
        verify(delegate).getDeliveryMode();
    }

    @Test
    public void setPriority() throws Exception {
        propagator.setPriority(1);
        verify(delegate).setPriority(1);
    }

    @Test
    public void getPriority() throws Exception {
        propagator.getPriority();
        verify(delegate).getPriority();
    }

    @Test
    public void setTimeToLive() throws Exception {
        propagator.setTimeToLive(1);
        verify(delegate).setTimeToLive(1);
    }

    @Test
    public void getTimeToLive() throws Exception {
        propagator.getTimeToLive();
        verify(delegate).getTimeToLive();
    }

    @Test
    public void getDestination() throws Exception {
        propagator.getDestination();
        verify(delegate).getDestination();
    }

    @Test
    public void close() throws Exception {
        propagator.close();
        verify(delegate).close();
    }

    @Test
    public void send() throws Exception {
        keys.stream().forEach(x -> current().put(x, x));
        propagator.send(message);
        verify(delegate).send(message);
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void send1() throws Exception {
        propagator.send(null, 1, 1, 1);
        verify(delegate).send(null, 1, 1, 1);
    }

    @Test
    public void send2() throws Exception {
        propagator.send(null, null);
        verify(delegate).send(null, null);
    }

    @Test
    public void send3() throws Exception {
        propagator.send(null, null, 1, 1, 1);
        verify(delegate).send(null, null, 1, 1, 1);
    }
}