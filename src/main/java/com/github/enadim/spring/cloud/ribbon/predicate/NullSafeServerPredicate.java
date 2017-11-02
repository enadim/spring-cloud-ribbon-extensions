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
package com.github.enadim.spring.cloud.ribbon.predicate;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import javax.annotation.Nullable;

/**
 * Convenient decorator avoiding the delegate to run against a null {@link PredicateKey} or a null {@link PredicateKey#getServer()}.
 *
 * @author Nadim Benabdenbi
 */
public abstract class NullSafeServerPredicate extends AbstractServerPredicate {

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean apply(@Nullable PredicateKey input) {
        return input != null && input.getServer() != null && doApply(input);
    }

    /**
     * Tests if {@link DiscoveryEnabledServer} matches this predicate.
     *
     * @param input the current server
     * @return {@code true} if the server matches the predicate otherwise {@code false}
     * @see #apply(PredicateKey)
     */
    protected abstract boolean doApply(PredicateKey input);


}
