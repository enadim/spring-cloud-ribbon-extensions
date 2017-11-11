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

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.propagator.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Collections.list;

/**
 * Copies Http Headers to the current {@link ExecutionContext} pre-filtering the header names using the provided {@link #filter}.
 *
 * @author Nadim Benabdenbi
 */
@Component
@Slf4j
public class PreservesHttpHeadersInterceptor implements HandlerInterceptor {
    /**
     * The request header names filter
     */
    private final Filter<String> filter;

    /**
     * Sole Constructor.
     *
     * @param filter The request header names filter.
     */
    public PreservesHttpHeadersInterceptor(@NotNull Filter<String> filter) {
        this.filter = filter;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        try {
            ExecutionContext context = current();
            //gather http header names
            ArrayList<String> headerNames = list(request.getHeaderNames());
            log.trace("Request Headers{}", headerNames);
            //filter header names to be copied
            List<String> eligibleHeaderNames = headerNames.stream()
                    .filter(filter::accept)
                    .collect(Collectors.toList());
            eligibleHeaderNames.forEach(x -> context.put(x, request.getHeader(x)));
            log.trace("Http header names copied to the execution context {}", eligibleHeaderNames);
        } catch (Exception e) {
            log.debug("Failed to copy http request header to the execution context.", e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        //nothing to do at this stage
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        //clean up thread local
        remove();
    }
}
