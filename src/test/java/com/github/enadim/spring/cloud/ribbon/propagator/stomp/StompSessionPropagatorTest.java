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

import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StompSessionPropagatorTest {
    private String destination = "destination";
    private StompHeaders headers = new StompHeaders();
    private Object payload = new Object();
    private Set<String> attributes = new HashSet<>(asList("1", "2"));
    private StompSession delegate = mock(StompSession.class);
    private StompSessionHandler handler = mock(StompSessionHandler.class);
    private StompSessionPropagator propagator = new StompSessionPropagator(delegate, attributes);

    @After
    public void after() {
        remove();
    }

    @Test
    public void testGetSessionId() throws Exception {
        propagator.getSessionId();
        verify(delegate).getSessionId();
    }

    @Test
    public void testIsConnected() throws Exception {
        propagator.isConnected();
        verify(delegate).isConnected();
    }

    @Test
    public void testSetAutoReceipt() throws Exception {
        propagator.setAutoReceipt(true);
        verify(delegate).setAutoReceipt(true);
    }

    @Test
    public void testSendEmptyContext() throws Exception {
        propagator.send(destination, payload);
        ArgumentHolder<StompHeaders> headers = new ArgumentHolder<>();
        verify(delegate).send(headers.eq(), eq(payload));
        attributes.forEach(x -> assertThat(headers.getArgument().get(x), is(nullValue())));
    }

    @Test
    public void testNotEmptyContext() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        propagator.send(destination, payload);
        ArgumentHolder<StompHeaders> headers = new ArgumentHolder<>();
        verify(delegate).send(headers.eq(), eq(payload));
        attributes.forEach(x -> assertThat(headers.getArgument().get(x), equalTo(asList(x))));
        assertThat(headers.getArgument().get("3"), is(nullValue()));
    }

    @Test
    public void testSendWithStompHeaders() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        propagator.send(headers, payload);
        verify(delegate).send(headers, payload);
        attributes.forEach(x -> assertThat(headers.get(x), equalTo(asList(x))));
        assertThat(headers.get("3"), is(nullValue()));
    }

    @Test
    public void testSubscribe() throws Exception {
        propagator.subscribe(destination, handler);
        verify(delegate).subscribe(eq(destination), any(StompFramePropagator.class));
    }

    @Test
    public void testSubscribeWithStompHeaders() throws Exception {
        propagator.subscribe(headers, handler);
        verify(delegate).subscribe(eq(headers), any(StompFramePropagator.class));
    }

    @Test
    public void testAcknowledge() throws Exception {
        propagator.acknowledge(null, true);
        verify(delegate).acknowledge(null, true);
    }

    @Test
    public void testDisconnect() throws Exception {
        propagator.disconnect();
        verify(delegate).disconnect();
    }


}