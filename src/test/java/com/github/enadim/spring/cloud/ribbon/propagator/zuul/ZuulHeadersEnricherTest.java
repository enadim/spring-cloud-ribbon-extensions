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
package com.github.enadim.spring.cloud.ribbon.propagator.zuul;

import com.netflix.zuul.context.RequestContext;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ZuulHeadersEnricherTest {
    Set<String> keys = new HashSet<>(Arrays.asList("1"));
    HashMap<String, String> extraHeaders = new HashMap<String, String>() {{
        put("1", "1");
        put("2", "2");
    }};
    ZuulHeadersEnricher enricher = new ZuulHeadersEnricher(keys::contains, extraHeaders);
    HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    public void preHandle() throws Exception {
        assertThat(enricher.preHandle(request, null, null), is(true));
        assertThat(RequestContext.getCurrentContext().getZuulRequestHeaders().get("1"), is("1"));
        assertThat(RequestContext.getCurrentContext().getZuulRequestHeaders().containsKey("2"), is(false));
    }

    @Test
    public void postHandle() throws Exception {
        enricher.postHandle(request, null, null, null);
    }

    @Test
    public void afterCompletion() throws Exception {
        enricher.afterCompletion(request, null, null, null);
    }

}