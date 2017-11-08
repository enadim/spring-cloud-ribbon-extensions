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

import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultConnectionFactoryPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultExecutorPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultFeignPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultHystrixContextPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultInboundHttpRequestPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultStompPropagationStrategy;
import com.github.enadim.spring.cloud.ribbon.support.ExecutionContextPropagationConfig.DefaultZuulPropagationStrategy;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExecutionContextPropagationImportTest {
    private ExecutionContextPropagationImport imports = new ExecutionContextPropagationImport();
    private AnnotationMetadata metadata = mock(AnnotationMetadata.class);

    @Test
    public void selectImports() throws Exception {
        assertThat(imports.selectImports(metadata).length, is(0));

        when(metadata.getAnnotationAttributes(EnableExecutionContextPropagation.class.getName(), true)).thenReturn(null);
        assertThat(imports.selectImports(metadata).length, is(0));

        assertThat(imports.selectImports(metadata).length, is(0));

        List<String> actual = Arrays.asList(imports.selectImports(new SimpleMetadataReaderFactory().getMetadataReader(Annotated.class.getName()).getAnnotationMetadata()));
        assertThat(actual, Matchers.containsInAnyOrder(
                DefaultInboundHttpRequestPropagationStrategy.class.getName(),
                DefaultFeignPropagationStrategy.class.getName(),
                DefaultExecutorPropagationStrategy.class.getName(),
                DefaultZuulPropagationStrategy.class.getName(),
                DefaultHystrixContextPropagationStrategy.class.getName(),
                DefaultConnectionFactoryPropagationStrategy.class.getName(),
                DefaultStompPropagationStrategy.class.getName()
        ));
    }

    @EnableExecutionContextPropagation
    static class Annotated {
    }

}