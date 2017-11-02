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

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import com.github.enadim.spring.cloud.ribbon.propagator.AbstractAttributesPropagator;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


/**
 * {@link RibbonRuleContext} Feign Propagator.
 * <p>Copies current {@link RibbonRuleContext} attributes to the feign template http headers.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class FeignHttpHeadersPropagator extends AbstractAttributesPropagator<RequestTemplate>
        implements RequestInterceptor {

    /**
     * Constructs the feign headers propagator
     *
     * @param keysToPropagate the attributes to propagate through http headers
     */
    public FeignHttpHeadersPropagator(@NotNull Set<String> keysToPropagate) {
        super(keysToPropagate, (x, y, z) -> x.header(y, z));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {
        List<Entry<String, String>> propagatedAttributes = propagate(template);
        log.trace("Propagated {}", propagatedAttributes);
    }
}
