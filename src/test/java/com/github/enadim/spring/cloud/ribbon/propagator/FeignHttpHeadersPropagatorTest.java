/**
 * Copyright (c) 2017 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.propagator;

import feign.RequestTemplate;
import org.junit.After;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class FeignHttpHeadersPropagatorTest {
    Set<String> attributes = new HashSet<>(asList("1", "2"));
    FeignHttpHeadersPropagator propagator = new FeignHttpHeadersPropagator(attributes);
    RequestTemplate requestTemplate = new RequestTemplate();

    @After
    public void after() {
        remove();
    }

    @Test
    public void do_nothing_on_empty_context() throws Exception {
        propagator.apply(requestTemplate);
        assertThat(requestTemplate.headers().size(), is(0));
    }

    @Test
    public void copy_headers() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        propagator.apply(requestTemplate);
        Map<String, Collection<String>> headers = requestTemplate.headers();
        asList("1", "2").forEach(x -> assertThat(headers.get(x), equalTo(asList(x))));
        assertThat(headers.containsKey("3"), is(false));
    }
}