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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
    private final Set<String> attributesToPropagate;

    public HttpRequestHeadersPropagator(final @NotNull Set<String> attributesToPropagate) {
        this.attributesToPropagate = attributesToPropagate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        try {
            RibbonRuleContext context = current();
            list(request.getHeaderNames()).stream()
                    .filter(x -> attributesToPropagate.contains(x))
                    .forEach(x -> {
                        context.put(x, request.getHeader(x));
                        log.trace("propagated {}={}", x, request.getHeader(x));
                    });
        } catch (Exception e) {
            log.warn("Failed to copy http request header to the ribbon filter context.", e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler,
                           final ModelAndView modelAndView) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final Object handler,
                                final Exception ex) throws Exception {
        remove();
    }
}
