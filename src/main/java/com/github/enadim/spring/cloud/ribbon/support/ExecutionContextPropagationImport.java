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
 * Registers the propagation strategies defined within {@link EnableExecutionContextPropagation}.
 *
 * @author Nadim Benabdenbi
 */
@Configuration
@Slf4j
public class ExecutionContextPropagationImport implements ImportSelector {

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        List<String> imports = new ArrayList<>();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableExecutionContextPropagation.class.getName(), true);
        if (attributes != null) {
            if ((boolean) attributes.getOrDefault("inboundHttpRequest", false)) {
                String strategy = (String) attributes.get("inboundHttpRequestStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled inbound http requests using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("feign", false)) {
                String strategy = (String) attributes.get("feignStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for feign using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("executor", false)) {
                String strategy = (String) attributes.get("executorStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for executors using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("zuul", false)) {
                String strategy = (String) attributes.get("zuulStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for zuul using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("hystrix", false)) {
                String strategy = (String) attributes.get("hystrixStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for hystrix using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("jms", false)) {
                String strategy = (String) attributes.get("jmsStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for jms using {}.", strategy);
            }
            if ((boolean) attributes.getOrDefault("stomp", false)) {
                String strategy = (String) attributes.get("stompStrategy");
                imports.add(strategy);
                log.info("Context propagation enabled for stomp using {}.", strategy);
            }
        }
        return imports.toArray(new String[imports.size()]);
    }
}
