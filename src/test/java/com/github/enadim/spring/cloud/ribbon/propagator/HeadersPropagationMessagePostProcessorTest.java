/**
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
package com.github.enadim.spring.cloud.ribbon.propagator;

import org.junit.After;
import org.junit.Test;

import javax.jms.Message;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HeadersPropagationMessagePostProcessorTest {
    Set<String> attributes = new HashSet<>(asList("1", "2"));
    PropagationJmsMessagePostProcessor processor = new PropagationJmsMessagePostProcessor(attributes);
    Message message = mock(Message.class);

    @Test
    public void empty_context() throws Exception {
        processor.postProcessMessage(message);
        verify(message, never()).setStringProperty(anyString(), anyString());
    }

    @Test
    public void not_empty_context() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        processor.postProcessMessage(message);
        verify(message).setStringProperty("1", "1");
        verify(message).setStringProperty("2", "2");
        verify(message, never()).setStringProperty("3", "3");
    }

    @After
    public void after() {
        remove();
    }
}