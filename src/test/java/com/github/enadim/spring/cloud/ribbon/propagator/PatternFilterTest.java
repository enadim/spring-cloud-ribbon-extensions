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
package com.github.enadim.spring.cloud.ribbon.propagator;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.regex.Pattern.compile;
import static org.assertj.core.api.Assertions.assertThat;

public class PatternFilterTest {
    String value1 = "12";
    String value2 = "23";
    Pattern any = compile(".*");
    Pattern pattern1 = compile(value1);
    Pattern pattern2 = compile(value2);

    @Test
    public void accept() throws Exception {
        assertThat(new PatternFilter().accept(value1)).isTrue();
        assertThat(new PatternFilter(singletonList(pattern1), emptyList()).accept(value1)).isTrue();
        assertThat(new PatternFilter(singletonList(pattern1), emptyList()).accept(value2)).isFalse();
        assertThat(new PatternFilter(singletonList(pattern1), singletonList(pattern2)).accept(value1)).isTrue();
        assertThat(new PatternFilter(singletonList(pattern1), singletonList(pattern2)).accept(value2)).isFalse();
        assertThat(new PatternFilter(singletonList(pattern1), singletonList(pattern2)).accept(value1 + value2)).isFalse();
        assertThat(new PatternFilter(asList(pattern1, pattern2), emptyList()).accept(value1)).isTrue();
        assertThat(new PatternFilter(asList(pattern1, pattern2), emptyList()).accept(value2)).isTrue();
        assertThat(new PatternFilter(asList(pattern1, pattern2), emptyList()).accept(value1 + value2)).isTrue();
        assertThat(new PatternFilter(asList(pattern1, pattern2), emptyList()).accept("")).isFalse();
    }

    @Test
    public void getIncludes() throws Exception {
        assertThat(new PatternFilter().getIncludes()).hasSize(1);
    }

    @Test
    public void getExcludes() throws Exception {
        assertThat(new PatternFilter().getExcludes()).isEmpty();
    }

}