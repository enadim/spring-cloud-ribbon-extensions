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
package com.github.enadim.spring.cloud.ribbon.support;


import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultConnectionFactoryPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultExecutorPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultFeignPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultHystrixContextPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultInboundHttpRequestPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultStompPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultZuulPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.PropagationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

/**
 * Enables {@link ExecutionContext} attributes propagation.
 * <p>Use it carefully due to {@link ExecutionContextHolder} usage of the {@link InheritableThreadLocal}:
 * This strategy will not cover all the use cases and you may encounter unexpected behaviours (Use it at your own risk):
 * <ul>
 * <li>Verify that {@link ExecutorService} are declared as beans and not hidden by some implementation.
 * <li>Verify that {@link org.springframework.jms.core.JmsTemplate} are declared as beans and not hidden by some implementation.
 * <li>Verify that {@link org.springframework.messaging.simp.stomp.StompSession} are declared as beans and not hidden by some implementation.
 * If you meet those requirements (and every dependency on your project) it will work perfectly.
 * </ul>
 * <p>To be used at spring boot configuration level. For example:
 * <blockquote><pre>
 * &#064;EnableExecutionContextPropagation
 * &#064;SpringBootApplication
 * public class Application{
 *  ...
 * }
 * </pre></blockquote>
 *
 * @author Nadim Benabdenbi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableConfigurationProperties(PropagationProperties.class)
@Import(ExecutionContextPropagationImport.class)
public @interface EnableExecutionContextPropagation {
    /**
     * default value is {@code true}.
     *
     * @return {@code true} when servlet propagation should be enabled otherwise {@code false}.
     */
    boolean inboundHttpRequest() default true;

    /**
     * default is {@link DefaultInboundHttpRequestPropagationStrategy}
     *
     * @return the servlet propagation strategy.
     */
    Class<?> inboundHttpRequestStrategy() default DefaultInboundHttpRequestPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when feign propagation should be enabled otherwise {@code false}.
     */
    boolean feign() default true;

    /**
     * default is {@link DefaultFeignPropagationStrategy}
     *
     * @return the feign propagation strategy.
     */
    Class<?> feignStrategy() default DefaultFeignPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when executor propagation should be enabled otherwise {@code false}.
     */
    boolean executor() default true;

    /**
     * default is {@link DefaultExecutorPropagationStrategy}.
     *
     * @return the executors propagation strategy.
     */
    Class<?> executorStrategy() default DefaultExecutorPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when zuul propagation should be enabled otherwise {@code false}.
     */
    boolean zuul() default true;

    /**
     * default is {@link DefaultZuulPropagationStrategy}
     *
     * @return the zuul propagation strategy.
     */
    Class<?> zuulStrategy() default DefaultZuulPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when Hystrix propagation should be enabled otherwise {@code false}.
     */
    boolean hystrix() default true;

    /**
     * default strategy is {@link DefaultHystrixContextPropagationStrategy}
     *
     * @return the hystrix propagation strategy.
     */
    Class<?> hystrixStrategy() default DefaultHystrixContextPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when jms propagation should be enabled otherwise {@code false}.
     */
    boolean jms() default true;

    /**
     * default strategy is {@link DefaultConnectionFactoryPropagationStrategy}.
     *
     * @return the jms propagation strategy.
     */
    Class<?> jmsStrategy() default DefaultConnectionFactoryPropagationStrategy.class;

    /**
     * default value is {@code true}.
     *
     * @return {@code true} when stomp propagation should be enabled otherwise {@code false}.
     */
    boolean stomp() default true;

    /**
     * default is {@link DefaultStompPropagationStrategy}
     *
     * @return the stomp propagation strategy.
     */
    Class<?> stompStrategy() default DefaultStompPropagationStrategy.class;
}
