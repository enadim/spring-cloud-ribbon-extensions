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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;

/**
 * {@link StompFrameHandler} propagator
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class StompFramePropagator implements StompFrameHandler {
    /**
     * the delegate {@link StompFrameHandler}
     */
    private final StompFrameHandler delegate;
    /**
     * the attribute keys to propagate
     */
    private final Set<String> keysToPropagate;

    /**
     * Sole constructor.
     *
     * @param delegate        the delegate {@link StompFrameHandler}
     * @param keysToPropagate the attribute keys to propagate
     */
    public StompFramePropagator(@NotNull StompFrameHandler delegate, @NotNull Set<String> keysToPropagate) {
        this.delegate = delegate;
        this.keysToPropagate = keysToPropagate;
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
        RibbonRuleContext context = current();
        List<Entry<String, String>> entries = headers.toSingleValueMap().entrySet().stream()
                .filter(x -> keysToPropagate.contains(x.getKey()))
                .collect(Collectors.toList());
        entries.forEach(x -> context.put(x.getKey(), x.getValue()));
        log.trace("propagated {}", entries);
        delegate.handleFrame(headers, payload);
        remove();
    }
}
