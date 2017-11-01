/**
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
package com.github.enadim.spring.cloud.ribbon.support.propagation.zuul;

import com.github.enadim.spring.cloud.ribbon.propagator.ExecutorServicePropagator;
import com.github.enadim.spring.cloud.ribbon.propagator.HystrixPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonContextPropagation;
import com.netflix.hystrix.strategy.HystrixPlugins;
import feign.RequestInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ContextPropagationZuulTest.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ContextPropagationZuulTest {

    @Inject
    ContextPropagationConfig config;
    @Inject
    RequestInterceptor requestInterceptor;
    @Inject
    MessagePostProcessor messagePostProcessor;
    @Inject
    ContextPropagationConfig.PropagationProperties properties;

    @Test
    public void test_configuration() {
        assertNotNull(properties);
        assertTrue(properties.isEnabled());
        assertNotNull(requestInterceptor);
        assertNotNull(messagePostProcessor);
        assertEquals(HystrixPlugins.getInstance().getConcurrencyStrategy().getClass(), HystrixPropagationStrategy.class);
    }

    @Test
    public void test_wrapExecutorService() {
        ExecutorService executorService = ContextPropagationConfig.wrapExecutorService(mock(ExecutorService.class));
        assertEquals(executorService.getClass(), ExecutorServicePropagator.class);
    }

    @SpringBootApplication
    @EnableZuulProxy
    @EnableRibbonContextPropagation
    public static class Application {
        @RequestMapping(method = GET)
        public String getMessage() {
            return "Message";
        }
    }
}