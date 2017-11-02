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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Copies Jms properties to the current {@link RibbonRuleContext}.
 * <p>Fails silently: enable trace logging for investigation.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class MessageListenerPropagator extends AbstractOnMessagePropagator implements MessageListener {
    /**
     * the delegate {@link MessageListener}
     */
    private final MessageListener delegate;

    /**
     * Sole constructor.
     *
     * @param delegate        the delegate {@link MessageListener}
     * @param keysToPropagate the attribute keys to propagate
     */
    public MessageListenerPropagator(@NotNull MessageListener delegate, @NotNull Set<String> keysToPropagate) {
        super(keysToPropagate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        delegate.onMessage(processMessage(message));
    }
}
