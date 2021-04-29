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
package com.github.enadim.spring.cloud.ribbon.support.strict;

import com.github.enadim.spring.cloud.ribbon.rule.PredicateBasedRuleSupport;
import com.github.enadim.spring.cloud.ribbon.support.StrictMetadataMatcherConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StrictMetadataMatcherConfig.class, properties = {
        "ribbon.client.name=application",
        "ribbon.extensions.client.application.rule.strict-metadata-matcher.enabled=false"})
public class StrictMetadataMatcherClientDisabledTest {

    @Inject
    ApplicationContext applicationContext;

    @Test()
    public void should_not_be_instantiated() {
        Assertions.assertThatThrownBy(() -> applicationContext.getBean(PredicateBasedRuleSupport.class)).isInstanceOf(NoSuchBeanDefinitionException.class);
    }
}