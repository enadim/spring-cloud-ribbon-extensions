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
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;

/**
 * Stomp frame handler adapter that copies stomp headers to the current {@link ExecutionContext} pre-filtering header names using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class HeadersCopyStompFrameHandlerAdapter implements StompFrameHandler {
    /**
     * The {@link StompFrameHandler} delegate.
     */
    private final StompFrameHandler delegate;
    /**
     * The stomp header names filter.
     */
    private final Filter<String> filter;

    /**
     * Sole constructor.
     *
     * @param delegate the delegate {@link StompFrameHandler}
     * @param filter   the stomp header names filter.
     */
    public HeadersCopyStompFrameHandlerAdapter(@NotNull StompFrameHandler delegate, @NotNull Filter<String> filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return delegate.getPayloadType(headers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ExecutionContext context = current();
        List<Entry<String, String>> eligibleHeaders = headers.toSingleValueMap().entrySet().stream()
                .filter(x -> filter.accept(x.getKey()))
                .collect(Collectors.toList());
        eligibleHeaders.forEach(x -> context.put(x.getKey(), x.getValue()));
        log.trace("Stomp Headers copied to execution context: {}.", eligibleHeaders);
        delegate.handleFrame(headers, payload);
        remove();
    }
}
