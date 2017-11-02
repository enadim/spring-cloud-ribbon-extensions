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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registers the desired propagation features defined at {@link EnableRibbonContextPropagation}.
 *
 * @author Nadim Benabdenbi
 */
@Configuration
@Slf4j
public class ContextPropagationImport implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        List<String> imports = new ArrayList<>();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableRibbonContextPropagation.class.getName(), true);
        if (attributes != null) {
            if ((boolean) attributes.getOrDefault("http", false)) {
                imports.add(ContextPropagationConfig.WebApplicationPropagationConfig.class.getName());
            }
            if ((boolean) attributes.getOrDefault("feign", false)) {
                imports.add(ContextPropagationConfig.FeignPropagationConfig.class.getName());
            }
            if ((boolean) attributes.getOrDefault("executor", false)) {
                imports.add(ContextPropagationConfig.ExecutorServicePostProcessor.class.getName());
                log.info("Propagation enabled for executors.");
            }
            if ((boolean) attributes.getOrDefault("zuul", false)) {
                imports.add(ContextPropagationConfig.ZuulHandlerBeanPostProcessor.class.getName());
                log.info("Propagation enabled for zuul.");
            }
            if ((boolean) attributes.getOrDefault("hystrix", false)) {
                imports.add(ContextPropagationConfig.HystrixRibbonContextPropagationConfig.class.getName());
            }
            if ((boolean) attributes.getOrDefault("jms", false)) {
                imports.add(ContextPropagationConfig.ConnectionFactoryPostProcessor.class.getName());
                log.info("Propagation enabled for jms.");
            }
            if ((boolean) attributes.getOrDefault("stomp", false)) {
                imports.add(ContextPropagationConfig.StompPropagationPostProcessor.class.getName());
                log.info("Propagation enabled for stomp.");
            }
        }
        return imports.toArray(new String[imports.size()]);
    }
}
