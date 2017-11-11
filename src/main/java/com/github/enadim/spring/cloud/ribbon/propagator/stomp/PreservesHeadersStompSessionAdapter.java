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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.propagator.AbstractExecutionContextCopy;
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stomp session adapter that preserves the {@link ExecutionContext} by copying stomp headers from/to the current {@link ExecutionContext} entry pre-filtering header names or entry keys using the provided {@link #filter}.
 */
@Slf4j
public class PreservesHeadersStompSessionAdapter extends AbstractExecutionContextCopy<StompHeaders> implements StompSession {
    /**
     * the delegate {@link StompSession}.
     */
    private final StompSession delegate;

    /**
     * Sole constructor.
     *
     * @param delegate           the delegate stomp session.
     * @param filter             the context entry key filter.
     * @param extraStaticEntries The extra static entries to copy.
     */
    public PreservesHeadersStompSessionAdapter(@NotNull StompSession delegate,
                                               @NotNull Filter<String> filter,
                                               @NotNull Map<String, String> extraStaticEntries) {
        super(filter, StompHeaders::set, extraStaticEntries);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSessionId() {
        return delegate.getSessionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return delegate.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoReceipt(boolean enabled) {
        delegate.setAutoReceipt(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Receiptable send(String destination, Object payload) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        return send(headers, payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Receiptable send(StompHeaders headers, Object payload) {
        List<Entry<String, String>> entries = copy(headers);
        log.trace("Execution context copied to stomp headers: {}.", entries);
        return delegate.send(headers, payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscription subscribe(String destination, StompFrameHandler handler) {
        return delegate.subscribe(destination, new PreservesHeadersStompFrameHandlerAdapter(handler, getFilter()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscription subscribe(StompHeaders headers, StompFrameHandler handler) {
        return delegate.subscribe(headers, new PreservesHeadersStompFrameHandlerAdapter(handler, getFilter()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Receiptable acknowledge(String messageId, boolean consumed) {
        return delegate.acknowledge(messageId, consumed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        delegate.disconnect();
    }

}
