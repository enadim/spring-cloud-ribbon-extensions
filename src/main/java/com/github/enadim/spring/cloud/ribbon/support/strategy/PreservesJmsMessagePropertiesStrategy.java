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

import com.github.enadim.spring.cloud.ribbon.propagator.jms.MessagePropertyEncoder;
import com.github.enadim.spring.cloud.ribbon.propagator.jms.PreservesMessagePropertiesConnectionFactoryAdapter;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

/**
 * Default jms propagation strategy based on jms propagationProperties copy from/to the execution context.
 */
@Configuration
@ConditionalOnClass(PreservesMessagePropertiesConnectionFactoryAdapter.class)
@ConditionalOnProperty(value = "ribbon.extensions.propagation.jms.enabled", matchIfMissing = true)
@ConditionalOnExpression(value = "${ribbon.extensions.propagation.enabled:true}")
@Slf4j
public class PreservesJmsMessagePropertiesStrategy extends InstantiationAwareBeanPostProcessorAdapter {
    @Autowired
    @Setter
    private PropagationProperties properties;
    @Autowired
    @Setter
    private EurekaInstanceProperties eurekaInstanceProperties;

    @Value("${ribbon.extensions.propagation.jms.encoder:com.github.enadim.spring.cloud.ribbon.propagator.jms.SimpleMessagePropertyEncoder}")
    @Setter
    private Class<? extends MessagePropertyEncoder> encoderType;

    private MessagePropertyEncoder encoder = null;

    private MessagePropertyEncoder getEncoder() {
        if (encoder == null) {
            try {
                encoder = encoderType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("message property encoder '" + encoderType + "' should be accessible with a default constructor");
            }
        }
        return encoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ConnectionFactory && !(bean instanceof PreservesMessagePropertiesConnectionFactoryAdapter)) {
            if (properties.getJms().accept(beanName)) {
                log.info("Context propagation enabled for jms connection factory [{}] on keys={}.", beanName, properties.getKeys());
                return new PreservesMessagePropertiesConnectionFactoryAdapter((ConnectionFactory) bean,
                        properties.buildEntriesFilter(),
                        properties.buildExtraStaticEntries(eurekaInstanceProperties),
                        getEncoder());
            } else {
                log.debug("Context propagation disabled for jms connection factory [{}]", beanName);
            }
        }
        return bean;
    }
}
