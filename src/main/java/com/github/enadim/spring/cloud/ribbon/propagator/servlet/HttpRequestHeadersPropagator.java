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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static java.util.Collections.list;

/**
 * Copies Http Headers to the current {@link RibbonRuleContext}.
 *
 * @author Nadim Benabdenbi
 */
@Component
@Slf4j
public class HttpRequestHeadersPropagator implements HandlerInterceptor {
    /**
     * the attribute keys to propagate
     */
    private final Set<String> keysToPropagate;

    /**
     * Sole Constructor.
     *
     * @param keysToPropagate the keys to propagate.
     */
    public HttpRequestHeadersPropagator(@NotNull Set<String> keysToPropagate) {
        this.keysToPropagate = keysToPropagate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        try {
            RibbonRuleContext context = current();
            ArrayList<String> headers = list(request.getHeaderNames());
            log.trace("Request Headers{}", headers);
            List<String> collected = headers.stream()
                    .filter(keysToPropagate::contains)
                    .collect(Collectors.toList());
            collected.forEach(x -> context.put(x, request.getHeader(x)));
            log.trace("Propagated {}", collected);
        } catch (Exception e) {
            log.warn("Failed to copy http request header to the ribbon filter context.", e);
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
        remove();
    }
}
