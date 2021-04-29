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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleStaticMetadataMatcherTest {
    private String attributeKey = "name";
    private String expectedValue = "value";
    private SingleStaticMetadataMatcher predicate = new SingleStaticMetadataMatcher(attributeKey, expectedValue);
    private InstanceInfo instanceInfo = mock(InstanceInfo.class);
    private Map<String, String> metada = new HashMap<>();
    private DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, true);

    @BeforeEach
    public void before() {
        when(instanceInfo.getMetadata()).thenReturn(metada);
    }

    @AfterEach
    public void after() {
        remove();
    }

    @Test
    public void should_not_filter_server_with_expected_attribute_value() throws Exception {
        metada.put(attributeKey, expectedValue);
        assertThat(predicate.doApply(server), is(true));
        assertThat(predicate.toString(), is("StrictMetadataMatcher[name=value]"));
    }

    @Test
    public void should_filter_server_with_no_attribute() throws Exception {
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_server_with_different_attribute_value() throws Exception {
        metada.put(attributeKey, attributeKey);
        assertThat(predicate.doApply(server), is(false));
    }
}