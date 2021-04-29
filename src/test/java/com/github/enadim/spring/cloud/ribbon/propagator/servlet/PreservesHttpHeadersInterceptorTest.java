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
package com.github.enadim.spring.cloud.ribbon.propagator.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreservesHttpHeadersInterceptorTest {
    Set<String> attributes = new HashSet<>(asList("1", "2"));
    PreservesHttpHeadersInterceptor propagator = new PreservesHttpHeadersInterceptor(attributes::contains);
    HttpServletRequest request = mock(HttpServletRequest.class);

    @BeforeEach
    public void before() {
        remove();
    }

    @Test
    public void should_skip_propagate_request_headers() throws Exception {
        when(request.getHeaderNames()).thenReturn(enumeration(asList("1", "2", "3")));
        attributes.forEach(x -> when(request.getHeader(x)).thenReturn(x));
        propagator.preHandle(request, null, null);
        attributes.forEach(x -> assertThat(current().get(x)).isEqualTo(x));
    }

    @Test
    public void should_skip_propagation_on_null_request_headers() throws Exception {
        when(request.getHeaderNames()).thenReturn(null);
        attributes.forEach(x -> when(request.getHeader(x)).thenReturn(x));
        propagator.preHandle(request, null, null);
        assertThat(current().entrySet()).isEmpty();
    }

    @Test
    public void fail_silent() throws Exception {
        propagator.preHandle(null, null, null);
    }

    @Test
    public void testPostHandle() throws Exception {
        propagator.postHandle(null, null, null, null);
        //nothing to test
    }

    @Test
    public void testAfterCompletion() throws Exception {
        current().put("1", "1");
        propagator.afterCompletion(null, null, null, null);
        assertThat(current().entrySet()).isEmpty();
    }


}