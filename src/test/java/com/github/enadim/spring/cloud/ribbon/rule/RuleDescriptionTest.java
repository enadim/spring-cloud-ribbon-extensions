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
package com.github.enadim.spring.cloud.ribbon.rule;

import com.netflix.loadbalancer.AbstractServerPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.enadim.spring.cloud.ribbon.rule.RuleDescription.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleDescriptionTest {
    AbstractServerPredicate predicate = mock(AbstractServerPredicate.class);
    String description = "description";
    String predicateDescription = "predicate";

    @BeforeEach
    public void before() {
        when(predicate.toString()).thenReturn(predicateDescription);
    }

    @Test
    public void from_string() {
        assertThat(from(description).describe()).isSameAs(description);
    }

    @Test
    public void from_predicate() throws Exception {
        assertThat(from(predicate).describe()).isSameAs(predicateDescription);
    }

    @Test
    public void and() throws Exception {
        assertThat(from(predicate).and(from(description)).describe()).isEqualTo("(" + predicateDescription + " && " + description + ")");
    }

    @Test
    public void fallback() throws Exception {
        assertThat(from(predicate).fallback(from(description)).describe()).isEqualTo(predicateDescription + " -> " + description);
    }

}