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

import com.github.enadim.spring.cloud.ribbon.propagator.jms.EchoMessagePropertyEncoder;
import com.github.enadim.spring.cloud.ribbon.propagator.jms.PreservesMessagePropertiesConnectionFactoryAdapter;
import com.github.enadim.spring.cloud.ribbon.propagator.stomp.PreservesHeadersStompSessionAdapter;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.jms.ConnectionFactory;

import static java.util.regex.Pattern.compile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class PreservesJmsMessagePropertiesStrategyTest {
    private String beanName = "name";
    private PreservesJmsMessagePropertiesStrategy processor = new PreservesJmsMessagePropertiesStrategy();

    @Test
    public void should_skip_bean() {
        Object bean = new Object();
        assertThat(processor.postProcessAfterInitialization(bean, beanName), is(bean));
    }

    @Test
    public void should_skip_propagator() {
        PreservesMessagePropertiesConnectionFactoryAdapter bean = new PreservesMessagePropertiesConnectionFactoryAdapter(null, null, null, new EchoMessagePropertyEncoder());
        assertThat(processor.postProcessAfterInitialization(bean, beanName), is(bean));
    }

    @Test
    public void should_decorate() {
        processor.setEncoderType(EchoMessagePropertyEncoder.class);
        processor.setProperties(new PropagationProperties());
        processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
        assertThat(processor.postProcessAfterInitialization(mock(ConnectionFactory.class), beanName).getClass(), equalTo(PreservesMessagePropertiesConnectionFactoryAdapter.class));
        processor.postProcessAfterInitialization(mock(ConnectionFactory.class), beanName);
    }

    @Test()
    public void should_fail_to_decorate_unaccessible_encoder_class() {
        Assertions.assertThatThrownBy(() -> {
            processor.setEncoderType(EchoMessagePropertyEncoder1.class);
            processor.setProperties(new PropagationProperties());
            processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
            processor.postProcessAfterInitialization(mock(ConnectionFactory.class), beanName);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_to_decorate_encoder_with_no_default_constructor() {
        Assertions.assertThatThrownBy(() -> {
            processor.setEncoderType(EchoMessagePropertyEncoder2.class);
            processor.setProperties(new PropagationProperties());
            processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
            processor.postProcessAfterInitialization(mock(ConnectionFactory.class), beanName);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_skip_stomp_session() {
        PropagationProperties propagationProperties = new PropagationProperties();
        propagationProperties.getJms().getExcludes().add(compile(beanName));
        processor.setProperties(propagationProperties);
        processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
        assertThat(processor.postProcessAfterInitialization(mock(ConnectionFactory.class), beanName).getClass(), not(equalTo(PreservesHeadersStompSessionAdapter.class)));
    }

    private static class EchoMessagePropertyEncoder1 extends EchoMessagePropertyEncoder {
    }

    public static class EchoMessagePropertyEncoder2 extends EchoMessagePropertyEncoder {
        public EchoMessagePropertyEncoder2(String toto) {
        }
    }
}