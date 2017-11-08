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
import javax.jms.MessageListener;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessagePropertiesCopyMessageListenerTest {
    MessageListener delegate = mock(MessageListener.class);
    MessagePropertiesCopyMessageListener listener = new MessagePropertiesCopyMessageListener(delegate, new HashSet<>()::contains);
    Message message = mock(Message.class);

    @Test
    public void onMessage() throws Exception {
        when(message.getPropertyNames()).thenReturn(Collections.emptyEnumeration());
        listener.onMessage(message);
        verify(delegate).onMessage(message);
    }

}