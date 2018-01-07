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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrictMetadataMatcherTest {
    StrictMetadataMatcher predicate = new StrictMetadataMatcher();
    InstanceInfo instanceInfo = mock(InstanceInfo.class);
    Map<String, String> metada = new HashMap<>();
    DiscoveryEnabledServer server = new DiscoveryEnabledServer(instanceInfo, true);

    @Before
    public void before() {
        remove();
        when(instanceInfo.getMetadata()).thenReturn(metada);
    }

    @After
    public void after() {
        remove();
        metada.clear();
    }

    @Test
    public void should_not_filter_server_having_exactly_the_sames_attributes() {
        asList("1", "2").forEach(x -> metada.put(x, x));
        asList("1", "2").forEach(x -> current().put(x, x));
        assertThat(predicate.doApply(server), is(true));
        assertThat(predicate.toString(), is("StrictMetadataMatcher[1=1, 2=2]"));
    }

    @Test
    public void should_not_filter_server_having_required_attributes() {
        asList("1", "2", "3").forEach(x -> metada.put(x, x));
        asList("1", "2").forEach(x -> current().put(x, x));
        assertThat(predicate.doApply(server), is(true));
        assertThat(predicate.toString(), is("StrictMetadataMatcher[1=1, 2=2]"));
    }

    @Test
    public void should_filter_server_having_a_missing_attributes() {
        asList("1", "3").forEach(x -> metada.put(x, x));
        asList("1", "2").forEach(x -> current().put(x, x));
        assertThat(predicate.doApply(server), is(false));
    }

    @Test
    public void should_filter_server_having_same_attributes_with_different_value() {
        asList("1", "2").forEach(x -> metada.put(x, x));
        metada.put("2", "3");
        asList("1", "2").forEach(x -> current().put(x, x));
        assertThat(predicate.doApply(server), is(false));
    }
}
