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
package com.github.enadim.spring.cloud.ribbon.rule;

import com.google.common.base.Optional;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PredicateBasedRuleSupportTest {

    AbstractServerPredicate predicate = mock(AbstractServerPredicate.class);
    ILoadBalancer loadBalancer = mock(ILoadBalancer.class);
    Server server = mock(Server.class);

    @Test
    public void testConstructor() {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        assertThat(support.getPredicate()).isSameAs(predicate);
    }

    @Test
    public void testSetPredicate() throws Exception {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport();
        assertThat(support.getPredicate()).isNull();
        support.setPredicate(predicate);
        assertThat(support.getPredicate()).isSameAs(predicate);
    }

    @Test
    public void shouldChooseServer() throws Exception {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        support.setLoadBalancer(loadBalancer);
        List<Server> servers = asList(server);
        when(loadBalancer.getAllServers()).thenReturn(servers);
        when(predicate.chooseRoundRobinAfterFiltering(servers, null)).thenReturn(Optional.of(server));
        assertThat(support.choose(null)).isSameAs(server);
    }

    @Test()
    public void shouldNotChooseServer() throws Exception {
        assertThatThrownBy(() -> {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        support.setLoadBalancer(loadBalancer);
        List<Server> servers = asList(server);
        when(loadBalancer.getAllServers()).thenReturn(servers);
        when(predicate.chooseRoundRobinAfterFiltering(servers, null)).thenReturn(Optional.fromNullable(null));
        support.choose(null);
        }).isInstanceOf(ChooseServerException.class);
    }
}