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

import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Zuul headers enricher.
 */
@Slf4j
public class ZuulHeadersEnricher implements HandlerInterceptor {
    /**
     * The extra headers to propagate.
     */
    private final Map<String, String> headers;
    /**
     * The propagation filter.
     */
    private final Filter<String> filter;

    /**
     * Sole constructor
     *
     * @param filter  the propagation filter
     * @param headers the extra headers to propagate.
     */
    public ZuulHeadersEnricher(Filter<String> filter, Map<String, String> headers) {
        this.headers = headers;
        this.filter = filter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<Entry<String, String>> result = new ArrayList<>();
        headers.entrySet().stream()
                .filter(x -> filter.accept(x.getKey()))
                .forEach(x -> {
                    RequestContext.getCurrentContext().addZuulRequestHeader(x.getKey(), x.getValue());
                    result.add(x);
                });
        log.trace("Propagated extra headers {}.", result);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {
        //nothing to do at this stage
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {
        //nothing to do at this stage
    }
}
