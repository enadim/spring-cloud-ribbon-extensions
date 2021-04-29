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
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSession;

import static java.util.regex.Pattern.compile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class PreservesStompHeadersStrategyTest {
    private String beanName = "name";
    private PreservesStompHeadersStrategy processor = new PreservesStompHeadersStrategy();

    @Test
    public void should_skip_bean() {
        Object bean = new Object();
        assertThat(processor.postProcessAfterInitialization(bean, beanName), is(bean));
    }

    @Test
    public void should_skip_propagator() {
        PreservesHeadersStompSessionAdapter bean = new PreservesHeadersStompSessionAdapter(null, null, null);
        assertThat(processor.postProcessAfterInitialization(bean, beanName), is(bean));
    }

    @Test
    public void should_decorate_stomp_session() {
        processor.setPropagationProperties(new PropagationProperties());
        processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
        assertThat(processor.postProcessAfterInitialization(mock(StompSession.class), beanName).getClass(), equalTo(PreservesHeadersStompSessionAdapter.class));
    }

    @Test
    public void should_skip_stomp_session() {
        PropagationProperties propagationProperties = new PropagationProperties();
        propagationProperties.getStomp().getExcludes().add(compile(beanName));
        processor.setPropagationProperties(propagationProperties);
        processor.setEurekaInstanceProperties(new EurekaInstanceProperties());
        assertThat(processor.postProcessAfterInitialization(mock(StompSession.class), beanName).getClass(), not(equalTo(PreservesHeadersStompSessionAdapter.class)));
    }
}