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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractMessagePropertiesCopyTest {
    private Set<String> keys = new HashSet<>(asList("1", "2"));
    private AbstractMessagePropertiesCopy propagator = new AbstractMessagePropertiesCopy(keys::contains) {
    };
    private Message message = mock(Message.class);

    @After
    public void after() {
        remove();
    }

    @Test
    public void copy_matching_properties() throws Exception {
        when(message.getPropertyNames()).thenReturn(Collections.enumeration(asList("1", "2", "3")));
        when(message.getStringProperty("1")).thenReturn("1");
        when(message.getStringProperty("2")).thenReturn("2");
        when(message.getStringProperty("3")).thenReturn("3");
        propagator.copyFromMessage(message);
        verify(message, never()).getStringProperty(eq("3"));
        keys.forEach(x -> Assert.assertThat(current().get(x), is(x)));
        Assert.assertThat(current().containsKey("3"), is(false));
    }

    @Test
    public void fail_on_get_property_names() throws Exception {
        when(message.getPropertyNames()).thenThrow(JMSException.class);
        propagator.copyFromMessage(message);
        Assert.assertThat(current().entrySet(), empty());
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
        Assert.assertThat(current().containsKey("1"), is(true));
        Assert.assertThat(current().entrySet().size(), is(1));
    }


}