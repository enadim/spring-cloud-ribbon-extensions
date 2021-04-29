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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AbstractPreservesMessagePropertiesTest {
    private Set<String> keys = new HashSet<>(asList("1", "2"));
    private AbstractPreservesMessageProperties propagator = new AbstractPreservesMessageProperties(keys::contains, new EchoMessagePropertyEncoder()) {
    };
    private Message message = mock(Message.class);

    @AfterEach
    public void after() {
        remove();
        reset(message);
    }

    @Test
    public void copy_matching_properties() throws Exception {
        when(message.getPropertyNames()).thenReturn(Collections.enumeration(asList("1", "2", "3")));
        when(message.getStringProperty("1")).thenReturn("1");
        when(message.getStringProperty("2")).thenReturn("2");
        when(message.getStringProperty("3")).thenReturn("3");
        propagator.copyFromMessage(message);
        verify(message, never()).getStringProperty(eq("3"));
        SoftAssertions soft = new SoftAssertions();
        keys.forEach(x -> soft.assertThat(current().get(x)).isEqualTo(x));
        soft.assertThat(current().containsKey("3")).isFalse();
    }

    @Test
    public void fail_on_get_property_names() throws Exception {
        when(message.getPropertyNames()).thenThrow(JMSException.class);
        propagator.copyFromMessage(message);
        assertThat(current().entrySet()).isEmpty();
    }

    @Test
    public void fail_on_get_property() throws Exception {
        when(message.getPropertyNames()).thenReturn(Collections.enumeration(asList("1", "2", "3")));
        when(message.getStringProperty("1")).thenReturn("1");
        when(message.getStringProperty("2")).thenThrow(JMSException.class);
        propagator.copyFromMessage(message);
        verify(message).getStringProperty(eq("1"));
        verify(message).getStringProperty(eq("2"));
        verify(message, never()).getStringProperty(eq("3"));
        assertThat(current().containsKey("1")).isTrue();
        assertThat(current().entrySet()).hasSize(1);
    }


}