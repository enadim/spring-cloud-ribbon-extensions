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


import com.netflix.appinfo.InstanceInfo;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.junit.Before;
import org.junit.Test;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstanceIdMatcherTest {
    String expected = "1";
    InstanceInfo instanceInfo = mock(InstanceInfo.class);
    DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, true);
    InstanceIdMatcher predicate = new InstanceIdMatcher(expected);

    @Before
    public void after() {
        remove();
    }

    @Test
    public void should_filter_when_favorite_zone_not_provided() {
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_when_instanceId_is_different() {
        when(instanceInfo.getInstanceId()).thenReturn("2");
        server.setZone("1");
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_when_instanceId_is_same() {
        when(instanceInfo.getInstanceId()).thenReturn(expected);
        server.setZone("1");
        assertThat(predicate.doApply(server), is(true));
    }
}