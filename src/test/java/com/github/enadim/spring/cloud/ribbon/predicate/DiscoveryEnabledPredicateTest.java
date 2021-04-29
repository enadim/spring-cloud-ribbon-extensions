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
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DiscoveryEnabledPredicateTest {

    DiscoveryEnabledServerPredicate predicate = mock(DiscoveryEnabledServerPredicate.class,
            withSettings().defaultAnswer(CALLS_REAL_METHODS));
    DiscoveryEnabledServer discoveryEnabledServer = mock(DiscoveryEnabledServer.class);
    Server server = mock(Server.class);


    @Test
    public void should_filter_non_discovery_server() throws Exception {
        when(predicate.doApply(any(DiscoveryEnabledServer.class))).thenReturn(true);
        assertThat(predicate.apply(new PredicateKey(server)), is(false));
    }


    @Test
    public void should_not_filter_when_discovery_server() throws Exception {
        when(predicate.doApply(any(DiscoveryEnabledServer.class))).thenReturn(true);
        assertThat(predicate.apply(new PredicateKey(discoveryEnabledServer)), is(true));
    }

    @Test
    public void should_filter_when_discovery_server() throws Exception {
        when(predicate.doApply(any(DiscoveryEnabledServer.class))).thenReturn(false);
        assertThat(predicate.apply(new PredicateKey(discoveryEnabledServer)), is(false));
    }
}