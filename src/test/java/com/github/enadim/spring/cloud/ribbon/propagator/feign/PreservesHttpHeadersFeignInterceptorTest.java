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
package com.github.enadim.spring.cloud.ribbon.propagator.feign;

import com.github.enadim.spring.cloud.ribbon.propagator.PatternFilter;
import feign.Request;
import feign.RequestTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class PreservesHttpHeadersFeignInterceptorTest {
    Set<String> keys = new HashSet<>(asList("1", "2"));
    String excludedUrl = "/some";
    PreservesHttpHeadersFeignInterceptor propagator = new PreservesHttpHeadersFeignInterceptor(new PatternFilter(asList(Pattern.compile(".*")), asList(Pattern.compile(excludedUrl))), keys::contains, new HashMap<>());
    RequestTemplate requestTemplate = new RequestTemplate();

    @Before
    public void before() {
        requestTemplate.target("http://google.com");
        requestTemplate.method(Request.HttpMethod.GET);
    }

    @After
    public void after() {
        remove();
    }

    @Test
    public void do_nothing_on_empty_context() throws Exception {
        requestTemplate = requestTemplate.resolve(Collections.emptyMap());
        propagator.apply(requestTemplate);
        assertThat(requestTemplate.headers().size(), is(0));
    }

    @Test
    public void do_nothing_on_excluded_url() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        requestTemplate.uri(excludedUrl);
        requestTemplate = requestTemplate.resolve(Collections.emptyMap());
        propagator.apply(requestTemplate);
        assertThat(requestTemplate.headers().size(), is(0));
    }

    @Test
    public void copy_headers() throws Exception {
        asList("1", "3", "2").forEach(x -> current().put(x, x));
        requestTemplate = requestTemplate.resolve(Collections.emptyMap());
        propagator.apply(requestTemplate);
        Map<String, Collection<String>> headers = requestTemplate.headers();
        asList("1", "2").forEach(x -> assertThat(headers.get(x), equalTo(asList(x))));
        assertThat(headers.containsKey("3"), is(false));
    }
}