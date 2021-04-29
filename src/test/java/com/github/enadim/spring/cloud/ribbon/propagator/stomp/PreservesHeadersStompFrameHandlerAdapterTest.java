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
package com.github.enadim.spring.cloud.ribbon.propagator.stomp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PreservesHeadersStompFrameHandlerAdapterTest {
    private Object payload = new Object();
    private Set<String> attributes = new HashSet<>(asList("1", "2"));
    private StompFrameHandler delegate = mock(StompFrameHandler.class);
    private PreservesHeadersStompFrameHandlerAdapter propagator = new PreservesHeadersStompFrameHandlerAdapter(delegate, attributes::contains);

    @BeforeEach
    public void before() {
        remove();
    }

    @Test
    public void testGetPayloadType() throws Exception {
        propagator.getPayloadType(null);
        verify(delegate).getPayloadType(null);
    }

    @Test
    public void testHandleFrame() throws Exception {
        StompHeaders headers = new StompHeaders();
        asList("1", "2", "3").forEach(x -> headers.set(x, x));
        propagator.handleFrame(headers, payload);
        verify(delegate).handleFrame(headers, payload);
    }
}