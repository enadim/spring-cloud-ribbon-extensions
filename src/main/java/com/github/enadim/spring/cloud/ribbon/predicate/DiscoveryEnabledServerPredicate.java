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

import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Convenient class for predicates that are based on {@link DiscoveryEnabledServer} created by the {@link DiscoveryEnabledNIWSServerList}.
 * {@link DiscoveryEnabledNIWSServerList}.
 * <p>Concrete implementation needs to implement the {@link #doApply(DiscoveryEnabledServer)} method.
 *
 * @author Nadim Benabdenbi
 */
public abstract class DiscoveryEnabledServerPredicate extends NullSafeServerPredicate {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
    protected boolean doApply(PredicateKey input) {
        return input.getServer() instanceof DiscoveryEnabledServer
                && doApply((DiscoveryEnabledServer) input.getServer());
    }

    /**
     * Tests if {@link DiscoveryEnabledServer} matches this predicate.
     *
     * @param server the discovered server
     * @return {@code true} if the server matches the predicate otherwise {@code false}
     * @see #apply(PredicateKey)
     */
    protected abstract boolean doApply(DiscoveryEnabledServer server);
}
