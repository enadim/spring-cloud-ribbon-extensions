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
package com.github.enadim.spring.cloud.ribbon.support.zuul;

import com.github.enadim.spring.cloud.ribbon.propagator.hystrix.ExecutionContextAwareHystrixStrategy;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EnableHttpLogging;
import com.github.enadim.spring.cloud.ribbon.support.PropagationProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import feign.RequestInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ContextPropagationZuulTest.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false"}
)
@EnableHttpLogging
public class ContextPropagationZuulTest {

    @Inject
    RequestInterceptor requestInterceptor;
    @Inject
    PropagationProperties properties;

    @Test
    public void test_configuration() {
        assertThat(properties).isNotNull();
        assertThat(requestInterceptor).isNotNull();
        assertThat(HystrixPlugins.getInstance().getConcurrencyStrategy().getClass()).isExactlyInstanceOf(
                ExecutionContextAwareHystrixStrategy.class);
    }

    @SpringBootApplication
    @EnableZuulProxy
    @EnableContextPropagation
    public static class Application {
        @RequestMapping(method = GET)
        public String getMessage() {
            return "Message";
        }
    }
}