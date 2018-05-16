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

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreservesMessagePropertiesSessionAdapterTest {
    Set<String> keys = new HashSet<>(asList("1"));
    Session delegate = mock(Session.class);
    PreservesMessagePropertiesSessionAdapter propagator = new PreservesMessagePropertiesSessionAdapter(delegate, keys::contains, new HashMap<>(), new EchoMessagePropertyEncoder());


    @Test
    public void createBytesMessage() throws Exception {
        BytesMessage message = mock(BytesMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createBytesMessage()).thenReturn(message);
        propagator.createBytesMessage();
        verify(delegate).createBytesMessage();
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createMapMessage() throws Exception {
        MapMessage message = mock(MapMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createMapMessage()).thenReturn(message);
        propagator.createMapMessage();
        verify(delegate).createMapMessage();
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createMessage() throws Exception {
        Message message = mock(Message.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createMessage()).thenReturn(message);
        propagator.createMessage();
        verify(delegate).createMessage();
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createObjectMessage() throws Exception {
        ObjectMessage message = mock(ObjectMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createObjectMessage()).thenReturn(message);
        propagator.createObjectMessage();
        verify(delegate).createObjectMessage();
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createObjectMessage1() throws Exception {
        ObjectMessage message = mock(ObjectMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createObjectMessage(null)).thenReturn(message);
        propagator.createObjectMessage(null);
        verify(delegate).createObjectMessage(null);
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createStreamMessage() throws Exception {
        StreamMessage message = mock(StreamMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createStreamMessage()).thenReturn(message);
        propagator.createStreamMessage();
        verify(delegate).createStreamMessage();
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void createTextMessage() throws Exception {
        TextMessage message = mock(TextMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createTextMessage()).thenReturn(message);
        propagator.createTextMessage();
        verify(delegate).createTextMessage();
        verify(message).setStringProperty("1", "1");

    }

    @Test
    public void createTextMessage1() throws Exception {
        TextMessage message = mock(TextMessage.class);
        keys.stream().forEach(x -> current().put(x, x));
        when(delegate.createTextMessage(null)).thenReturn(message);
        propagator.createTextMessage(null);
        verify(delegate).createTextMessage(null);
        verify(message).setStringProperty("1", "1");
    }

    @Test
    public void getTransacted() throws Exception {
        propagator.getTransacted();
        verify(delegate).getTransacted();
    }

    @Test
    public void getAcknowledgeMode() throws Exception {
        propagator.getAcknowledgeMode();
        verify(delegate).getAcknowledgeMode();
    }

    @Test
    public void commit() throws Exception {
        propagator.commit();
        verify(delegate).commit();
    }

    @Test
    public void rollback() throws Exception {
        propagator.rollback();
        verify(delegate).rollback();
    }

    @Test
    public void close() throws Exception {
        propagator.close();
        verify(delegate).close();
    }

    @Test
    public void recover() throws Exception {
        propagator.recover();
        verify(delegate).recover();
    }

    @Test
    public void getMessageListener() throws Exception {
        propagator.getMessageListener();
        verify(delegate).getMessageListener();
    }

    @Test
    public void setMessageListener() throws Exception {
        propagator.setMessageListener(mock(MessageListener.class));
        verify(delegate).setMessageListener(any(PreservesMessagePropertiesMessageListener.class));
        reset(delegate);
        propagator.setMessageListener(new PreservesMessagePropertiesMessageListener(mock(MessageListener.class), null, new EchoMessagePropertyEncoder()));
        verify(delegate).setMessageListener(any(PreservesMessagePropertiesMessageListener.class));
    }

    @Test
    public void run() throws Exception {
        propagator.run();
        verify(delegate).run();
    }

    @Test
    public void createProducer() throws Exception {
        propagator.createProducer(null);
        verify(delegate).createProducer(null);
    }

    @Test
    public void createConsumer() throws Exception {
        propagator.createConsumer(null);
        verify(delegate).createConsumer(null);
    }

    @Test
    public void createConsumer1() throws Exception {
        propagator.createConsumer(null, null);
        verify(delegate).createConsumer(null, null);
    }

    @Test
    public void createConsumer2() throws Exception {
        propagator.createConsumer(null, null, true);
        verify(delegate).createConsumer(null, null, true);
    }

    @Test
    public void createQueue() throws Exception {
        propagator.createQueue(null);
        verify(delegate).createQueue(null);
    }

    @Test
    public void createTopic() throws Exception {
        propagator.createTopic(null);
        verify(delegate).createTopic(null);
    }

    @Test
    public void createDurableSubscriber() throws Exception {
        propagator.createDurableSubscriber(null, null);
        verify(delegate).createDurableSubscriber(null, null);
    }

    @Test
    public void createDurableSubscriber1() throws Exception {
        propagator.createDurableSubscriber(null, null, null, true);
        verify(delegate).createDurableSubscriber(null, null, null, true);
    }

    @Test
    public void createBrowser() throws Exception {
        propagator.createBrowser(null);
        verify(delegate).createBrowser(null);
    }

    @Test
    public void createBrowser1() throws Exception {
        propagator.createBrowser(null, null);
        verify(delegate).createBrowser(null, null);
    }

    @Test
    public void createTemporaryQueue() throws Exception {
        propagator.createTemporaryQueue();
        verify(delegate).createTemporaryQueue();
    }

    @Test
    public void createTemporaryTopic() throws Exception {
        propagator.createTemporaryTopic();
        verify(delegate).createTemporaryTopic();
    }

    @Test
    public void unsubscribe() throws Exception {
        propagator.unsubscribe(null);
        verify(delegate).unsubscribe(null);
    }

    @Test
    public void createSharedConsumer2() throws Exception {
        propagator.createSharedConsumer(null, null);
        verify(delegate).createSharedConsumer(null, null);
    }

    @Test
    public void createSharedConsumer3() throws Exception {
        propagator.createSharedConsumer(null, null, null);
        verify(delegate).createSharedConsumer(null, null, null);
    }

    @Test
    public void createDurableConsumer2() throws Exception {
        propagator.createDurableConsumer(null, null);
        verify(delegate).createDurableConsumer(null, null);
    }

    @Test
    public void createDurableConsumer4() throws Exception {
        propagator.createDurableConsumer(null, null, null, true);
        verify(delegate).createDurableConsumer(null, null, null, true);
    }

    @Test
    public void createSharedDurableConsumer2() throws Exception {
        propagator.createSharedDurableConsumer(null, null);
        verify(delegate).createSharedDurableConsumer(null, null);
    }

    @Test
    public void createSharedDurableConsumer3() throws Exception {
        propagator.createSharedDurableConsumer(null, null, null);
        verify(delegate).createSharedDurableConsumer(null, null, null);
    }
}