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

import org.junit.jupiter.api.Test;

import javax.jms.Message;
import javax.jms.MessageProducer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PreservesMessagePropertiesMessageProducerAdapterTest {
    Set<String> keys = new HashSet<>(asList("1"));
    MessageProducer delegate = mock(MessageProducer.class);
    PreservesMessagePropertiesMessageProducerAdapter propagator = new PreservesMessagePropertiesMessageProducerAdapter(delegate, keys::contains, new HashMap<>(), new EchoMessagePropertyEncoder());
    Message message = mock(Message.class);

    @Test
    public void setDeliveryDelay() throws Exception {
        propagator.setDeliveryDelay(0);
        verify(delegate).setDeliveryDelay(0);
    }

    @Test
    public void getDeliveryDelay() throws Exception {
        propagator.getDeliveryDelay();
        verify(delegate).getDeliveryDelay();
    }

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
        propagator.send(message, 1, 1, 1);
        verify(delegate).send(message, 1, 1, 1);
    }

    @Test
    public void send2() throws Exception {
        propagator.send(null, message);
        verify(delegate).send(null, message);
    }

    @Test
    public void send3() throws Exception {
        propagator.send(null, message, 1, 1, 1);
        verify(delegate).send(null, message, 1, 1, 1);
    }

    @Test
    public void send4() throws Exception {
        propagator.send(message, null);
        verify(delegate).send(message, null);
    }

    @Test
    public void send5() throws Exception {
        propagator.send(null, message, null);
        verify(delegate).send(null, message, null);
    }

    @Test
    public void send6() throws Exception {
        propagator.send(message, 0, 0, 0, null);
        verify(delegate).send(message, 0, 0, 0, null);
    }

    @Test
    public void send7() throws Exception {
        propagator.send(null, message, 0, 0, 0, null);
        verify(delegate).send(null, message, 0, 0, 0, null);
    }
}