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
package com.github.enadim.spring.cloud.ribbon.predicate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaticMatcherTest {
    private String attributeName = "name";
    private String expectedValue = "value";
    private StaticMatcher predicate = new StaticMatcher(attributeName, expectedValue);
    private InstanceInfo instanceInfo = mock(InstanceInfo.class);
    private Map<String, String> metada = new HashMap<>();
    private DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, true);

    @Before
    public void before() {
        when(instanceInfo.getMetadata()).thenReturn(metada);
    }

    @Test
    public void should_not_filter_server_with_expected_attribute_value() throws Exception {
        metada.put(attributeName, expectedValue);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void should_filter_server_with_no_attribute() throws Exception {
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_server_with_different_attribute_value() throws Exception {
        metada.put(attributeName, attributeName);
        assertThat(predicate.doApply(server), is(false));
    }
}