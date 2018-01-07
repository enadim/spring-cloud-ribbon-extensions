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

import com.github.enadim.spring.cloud.ribbon.ArgumentHolder;
import org.junit.After;
import org.junit.Test;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PreservesHeadersStompSessionAdapterTest {
    private String destination = "destination";
    private StompHeaders headers = new StompHeaders();
    private Object payload = new Object();
    private Set<String> keysToCopy = new HashSet<>(asList("1", "2"));
    private StompSession delegate = mock(StompSession.class);
    private StompSessionHandler handler = mock(StompSessionHandler.class);
    private PreservesHeadersStompSessionAdapter propagator = new PreservesHeadersStompSessionAdapter(delegate, keysToCopy::contains, new HashMap<>());

    @After
    public void after() {
        remove();
    }

    @Test
    public void testGetSessionId() {
        propagator.getSessionId();
        verify(delegate).getSessionId();
    }

    @Test
    public void testIsConnected() {
        propagator.isConnected();
        verify(delegate).isConnected();
    }

    @Test
    public void testSetAutoReceipt() {
        propagator.setAutoReceipt(true);
        verify(delegate).setAutoReceipt(true);
    }

    @Test
    public void testSendEmptyContext() {
        propagator.send(destination, payload);
        ArgumentHolder<StompHeaders> headers = new ArgumentHolder<>();
        verify(delegate).send(headers.eq(), eq(payload));
        keysToCopy.forEach(x -> assertThat(headers.getArgument().get(x), is(nullValue())));
    }

    @Test
    public void testNotEmptyContext() {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        propagator.send(destination, payload);
        ArgumentHolder<StompHeaders> headers = new ArgumentHolder<>();
        verify(delegate).send(headers.eq(), eq(payload));
        keysToCopy.forEach(x -> assertThat(headers.getArgument().get(x), equalTo(asList(x))));
        assertThat(headers.getArgument().get("3"), is(nullValue()));
    }

    @Test
    public void testSendWithStompHeaders() {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        propagator.send(headers, payload);
        verify(delegate).send(headers, payload);
        keysToCopy.forEach(x -> assertThat(headers.get(x), equalTo(asList(x))));
        assertThat(headers.get("3"), is(nullValue()));
    }

    @Test
    public void testSubscribe() {
        propagator.subscribe(destination, handler);
        verify(delegate).subscribe(eq(destination), any(PreservesHeadersStompFrameHandlerAdapter.class));
    }

    @Test
    public void testSubscribeWithStompHeaders() {
        propagator.subscribe(headers, handler);
        verify(delegate).subscribe(eq(headers), any(PreservesHeadersStompFrameHandlerAdapter.class));
    }

    @Test
    public void testAcknowledgeMessageId() {
        propagator.acknowledge("", true);
        verify(delegate).acknowledge("", true);
    }

    @Test
    public void testAcknowledgeHeaders() {
        propagator.acknowledge(new StompHeaders(), true);
        verify(delegate).acknowledge(new StompHeaders(), true);
    }

    @Test
    public void testDisconnect() {
        propagator.disconnect();
        verify(delegate).disconnect();
    }


}