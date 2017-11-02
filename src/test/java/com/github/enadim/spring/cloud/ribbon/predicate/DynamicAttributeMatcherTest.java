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

import java.util.HashMap;
import java.util.Map;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DynamicAttributeMatcherTest {
    String dynamicAttributeKey = "dynamic";
    String attributeKey = "key";
    String attributeValue = "value";
    InstanceInfo instanceInfo = mock(InstanceInfo.class);
    Map<String, String> metada = new HashMap<>();
    DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, true);
    DynamicAttributeMatcher predicate = new DynamicAttributeMatcher(dynamicAttributeKey);

    @Before
    public void before() {
        when(instanceInfo.getMetadata()).thenReturn(metada);
    }

    @Before
    public void after() {
        remove();
    }

    @Test
    public void should_filter_server_when_different_values() throws Exception {
        metada.put(attributeKey, attributeValue);
        current().put(dynamicAttributeKey, attributeKey);
        current().put(attributeKey, "");
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_server_when_expected_is_not_defined() throws Exception {
        metada.put(attributeKey, attributeValue);
        current().put(dynamicAttributeKey, attributeKey);
        current().put(attributeKey, null);
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_server_when_dynamic_key_not_defined() throws Exception {
        metada.put(attributeKey, attributeValue);
        current().put(attributeKey, attributeValue);
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_not_filter_server_with_same_attribute_value() throws Exception {
        metada.put(attributeKey, attributeValue);
        current().put(dynamicAttributeKey, attributeKey);
        current().put(attributeKey, attributeValue);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void should_not_filter_server_with_same_null_attribute_value() throws Exception {
        metada.put(attributeKey, null);
        current().put(dynamicAttributeKey, attributeKey);
        current().put(attributeKey, null);
        assertThat(predicate.doApply(server), is(true));
    }


}