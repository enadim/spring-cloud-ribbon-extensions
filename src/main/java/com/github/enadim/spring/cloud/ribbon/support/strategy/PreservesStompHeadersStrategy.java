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
package com.github.enadim.spring.cloud.ribbon.support.strategy;

import com.github.enadim.spring.cloud.ribbon.propagator.stomp.PreservesHeadersStompSessionAdapter;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompSession;

/**
 * Default Stomp propagation strategy based on stomp headers copy from/to the execution context.
 */
@Configuration
@ConditionalOnClass(StompSession.class)
@ConditionalOnProperty(value = "ribbon.extensions.propagation.stomp.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
@Slf4j
public class PreservesStompHeadersStrategy extends InstantiationAwareBeanPostProcessorAdapter {
    @Autowired
    @Setter
    private PropagationProperties propagationProperties;
    @Autowired
    @Setter
    private EurekaInstanceProperties eurekaInstanceProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof StompSession && !(bean instanceof PreservesHeadersStompSessionAdapter)) {
            if (propagationProperties.getStomp().accept(beanName)) {
                log.info("Context propagation enabled for stomp session [{}] on keys={}.", beanName, propagationProperties.getKeys());
                return new PreservesHeadersStompSessionAdapter((StompSession) bean,
                        propagationProperties.buildEntriesFilter(),
                        propagationProperties.buildExtraStaticEntries(eurekaInstanceProperties));
            } else {
                log.debug("Context propagation disabled for stomp session [{}]", beanName);
            }
        }
        return bean;
    }
}
