/**
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AttributeMatcherTest {
    String                 attributeName = "name";
    String                 defaultValue  = "value";
    InstanceInfo           instanceInfo  = mock(InstanceInfo.class);
    Map<String, String>    metada        = new HashMap<>();
    DiscoveryEnabledServer server        = new DiscoveryEnabledServer(instanceInfo, true);

    @Before
    public void before() {
        when(instanceInfo.getMetadata()).thenReturn(metada);
    }

    @Before
    public void after() {
        remove();
    }

    @Test
    public void with_no_default_value_should_not_filter_server_when_empty_context() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, null);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void with_no_default_value_should_not_filter_server_with_same_attribute_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, null);
        metada.put(attributeName, defaultValue);
        current().put(attributeName, defaultValue);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void with_no_default_value_should_filter_server_with_different_attribute_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, null);
        metada.put(attributeName, attributeName);
        current().put(attributeName, defaultValue);
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void with_default_value_should_not_filter_server_with_same_default_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, defaultValue);
        metada.put(attributeName, defaultValue);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void with_default_value_should_filter_server_with_different_default_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, defaultValue);
        metada.put(attributeName, attributeName);
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void with_default_value_should_not_filter_server_with_same_context_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, defaultValue);
        metada.put(attributeName, defaultValue);
        current().put(attributeName, defaultValue);
        assertThat(predicate.doApply(server), is(true));
    }

    @Test
    public void with_default_value_should_filter_server_with_different_context_value() throws Exception {
        AttributeMatcher predicate = new AttributeMatcher(attributeName, defaultValue);
        metada.put(attributeName, defaultValue);
        current().put(attributeName, attributeName);
        assertThat(predicate.doApply(server), is(false));
    }
}