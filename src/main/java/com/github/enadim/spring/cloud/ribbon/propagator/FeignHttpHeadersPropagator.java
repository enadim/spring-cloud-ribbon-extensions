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
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Set;


/**
 * {@link RibbonRuleContext} Feign Propagator.
 * <p>Copies current {@link RibbonRuleContext} attributes to the feign template http headers.
 *
 * @author Nadim Benabdenbi
 */
@Slf4j
public class FeignHttpHeadersPropagator extends AbstractAttributesPropagator<RequestTemplate> implements RequestInterceptor {

    /**
     * Constructs the feign headers propagator
     *
     * @param attributesToPropagate the attributes to propagate through http headers
     */
    public FeignHttpHeadersPropagator(@NotNull Set<String> attributesToPropagate) {
        super(attributesToPropagate, (x, y, z) -> x.header(y, z));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {
        log.trace("propagated {}", propagate(template));
    }
}
