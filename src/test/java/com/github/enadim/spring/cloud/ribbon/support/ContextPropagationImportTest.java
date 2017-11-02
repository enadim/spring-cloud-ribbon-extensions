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

import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ConnectionFactoryPostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ExecutorServicePostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.FeignPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.HystrixRibbonContextPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.StompPropagationPostProcessor;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.WebApplicationPropagationConfig;
import com.github.enadim.spring.cloud.ribbon.support.ContextPropagationConfig.ZuulHandlerBeanPostProcessor;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContextPropagationImportTest {
    ContextPropagationImport imports = new ContextPropagationImport();
    AnnotationMetadata metadata = mock(AnnotationMetadata.class);
    Map<String, Object> attributes = new HashMap<>();

    @Test
    public void selectImports() throws Exception {
        assertThat(imports.selectImports(metadata).length, is(0));

        when(metadata.getAnnotationAttributes(EnableRibbonContextPropagation.class.getName(), true)).thenReturn(null);
        assertThat(imports.selectImports(metadata).length, is(0));

        when(metadata.getAnnotationAttributes(EnableRibbonContextPropagation.class.getName(), true)).thenReturn(attributes);
        assertThat(imports.selectImports(metadata).length, is(0));

        attributes.put("http", true);
        attributes.put("feign", true);
        attributes.put("executor", true);
        attributes.put("zuul", true);
        attributes.put("hystrix", true);
        attributes.put("jms", true);
        attributes.put("stomp", true);
        assertThat(Arrays.asList(imports.selectImports(metadata)), Matchers.containsInAnyOrder(
                WebApplicationPropagationConfig.class.getName(),
                FeignPropagationConfig.class.getName(),
                ExecutorServicePostProcessor.class.getName(),
                ZuulHandlerBeanPostProcessor.class.getName(),
                HystrixRibbonContextPropagationConfig.class.getName(),
                ConnectionFactoryPostProcessor.class.getName(),
                StompPropagationPostProcessor.class.getName()
        ));
    }

}