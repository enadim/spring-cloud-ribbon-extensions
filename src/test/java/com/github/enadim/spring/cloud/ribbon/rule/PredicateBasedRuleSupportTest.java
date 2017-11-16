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
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PredicateBasedRuleSupportTest {

    AbstractServerPredicate predicate = mock(AbstractServerPredicate.class);
    ILoadBalancer loadBalancer = mock(ILoadBalancer.class);
    Server server = mock(Server.class);

    @Test
    public void testConstructor() {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        Assert.assertThat(support.getPredicate(), is(predicate));
    }

    @Test
    public void testSetPredicate() throws Exception {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport();
        Assert.assertThat(support.getPredicate(), is(nullValue()));
        support.setPredicate(predicate);
        Assert.assertThat(support.getPredicate(), is(predicate));
    }

    @Test
    public void shouldChooseServer() throws Exception {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        support.setLoadBalancer(loadBalancer);
        List<Server> servers = asList(server);
        when(loadBalancer.getAllServers()).thenReturn(servers);
        when(predicate.chooseRoundRobinAfterFiltering(servers, null)).thenReturn(Optional.of(server));
        Assert.assertThat(support.choose(null), is(server));
    }

    @Test(expected = ChooseServerException.class)
    public void shouldNotChooseServer() throws Exception {
        PredicateBasedRuleSupport support = new PredicateBasedRuleSupport(predicate);
        support.setLoadBalancer(loadBalancer);
        List<Server> servers = asList(server);
        when(loadBalancer.getAllServers()).thenReturn(servers);
        when(predicate.chooseRoundRobinAfterFiltering(servers, null)).thenReturn(Optional.fromNullable(null));
        support.choose(null);
    }
}