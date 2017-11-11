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
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.lastIndexOfAny;
import static org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase;

/**
 * Registers the propagation strategies defined within {@link EnableContextPropagation}.
 *
 * @author Nadim Benabdenbi
 */
@Configuration
@Slf4j
public class ExecutionContextPropagationImport implements ImportSelector {
    /**
     * class name separators
     */
    private static final String[] CLASS_NAME_SEPARATORS = new String[]{"$", "."};
    /**
     * class name separators
     */
    private static final List<String> ATTRIBUTES = asList("inboundHttpRequest", "feign", "executor", "zuul", "hystrix", "jms", "stomp");

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        List<String> imports = new ArrayList<>();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableContextPropagation.class.getName(), true);
        if (attributes != null) {
            ATTRIBUTES.forEach(x -> importStrategy(imports, attributes, x));
        }
        return imports.toArray(new String[imports.size()]);
    }

    /**
     * Adds the strategy to the import list when enabled.
     *
     * @param imports    the import list
     * @param attributes the annotation attributes
     * @param name       the strategy name
     */
    private void importStrategy(List<String> imports, Map<String, Object> attributes, String name) {
        if ((boolean) attributes.getOrDefault(name, false)) {
            String strategy = (String) attributes.get(new StringBuilder(name).append("Strategy").toString());
            imports.add(strategy);
            log.info("Context propagation: importing {} strategy [{}].",
                    join(splitByCharacterTypeCamelCase(name), ' ').toLowerCase(Locale.ENGLISH),
                    strategy.substring(lastIndexOfAny(strategy, CLASS_NAME_SEPARATORS) + 1));
        }
    }
}
