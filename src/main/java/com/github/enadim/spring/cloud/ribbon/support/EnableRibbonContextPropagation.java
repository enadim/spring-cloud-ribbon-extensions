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


import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ConnectionFactoryPostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ExecutorServicePostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.FeignPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.HystrixRibbonContextPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.PropagationProperties;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.StompPropagationPostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.WebApplicationPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ZuulHandlerBeanPostProcessor;
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
 * Enables {@link RibbonRuleContext} attributes propagation.
 * <p>Use it carefully due to {@link RibbonRuleContextHolder} usage of the {@link InheritableThreadLocal}:
 * This strategy will not cover all the use cases and you may encounter unexpected behaviours (Use it at your own risk):
 * <ul>
 * <li>Verify that {@link ExecutorService} are declared as beans and not hidden by some implementation.
 * <li>Verify that {@link org.springframework.jms.core.JmsTemplate} are declared as beans and not hidden by some implementation.
 * <li>Verify that {@link org.springframework.messaging.simp.stomp.StompSession} are declared as beans and not hidden by some implementation.
 * If you meet those requirements (and every dependency on your project) it will work perfectly.
 * </ul>
 * <p>To be used at spring boot configuration level. For example:
 * <blockquote><pre>
 * &#064;EnableRibbonContextPropagation
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
@Import(ContextPropagationImport.class)
public @interface EnableRibbonContextPropagation {
    /**
     * default value is true.
     *
     * @return true when inbound http request headers should be propagated otherwise false.
     */
    boolean http() default true;

    /**
     * @return the inbound http request propagation configuration. default is {@link WebApplicationPropagationConfig}
     */
    Class<?> httpConfiguration() default WebApplicationPropagationConfig.class;

    /**
     * default value is true.
     *
     * @return true when outbound feign request should copies the context attribute to the request headers otherwise false.
     */
    boolean feign() default true;

    /**
     * @return the outbound feign request propagation configuration. default is {@link FeignPropagationConfig}
     */
    Class<?> feignConfiguration() default FeignPropagationConfig.class;

    /**
     * default value is true.
     *
     * @return true when executors should propagate the context to any submitted task otherwise false.
     */
    boolean executor() default true;

    /**
     * @return the executors propagation configuration. default is {@link ExecutorServicePostProcessor}
     */
    Class<?> executorConfiguration() default ExecutorServicePostProcessor.class;

    /**
     * default value is true.
     *
     * @return true when zuul should propagate the http request headers otherwise false.
     */
    boolean zuul() default true;

    /**
     * @return the zuul propagation configuration. default is {@link ZuulHandlerBeanPostProcessor}
     */
    Class<?> zuulConfiguration() default ZuulHandlerBeanPostProcessor.class;

    /**
     * @return true when hystrix should propagate the context to any async task otherwise false.
     */
    boolean hystrix() default true;

    /**
     * @return the hystrix propagation configuration. default is {@link HystrixRibbonContextPropagationConfig}
     */
    Class<?> hystrixConfiguration() default HystrixRibbonContextPropagationConfig.class;

    /**
     * default value is true.
     *
     * @return true when jms message properties and the context attributes should be in sync (inbound &amp; outbound) otherwise false.
     */
    boolean jms() default true;

    /**
     * @return the jms propagation configuration. default is {@link ConnectionFactoryPostProcessor}
     */
    Class<?> jmsConfiguration() default ConnectionFactoryPostProcessor.class;

    /**
     * default value is true.
     *
     * @return true when stomp headers and the context attributes should be in sync (inbound &amp; outbound) otherwise false.
     */
    boolean stomp() default true;

    /**
     * @return the stomp propagation configuration. default is {@link StompPropagationPostProcessor}
     */
    Class<?> stompConfiguration() default StompPropagationPostProcessor.class;
}
