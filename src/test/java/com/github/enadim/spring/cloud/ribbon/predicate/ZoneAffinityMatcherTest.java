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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ZoneAffinityMatcherTest {

    String expectedZone = "zone1";
    ZoneAffinityMatcher predicate = new ZoneAffinityMatcher(expectedZone);
    Server server = new Server("id");
    PredicateKey predicateKey = new PredicateKey(server);

    @AfterEach
    public void after() {
        remove();
    }

    @Test
    public void should_filter_when_server_zone_is_different() {
        server.setZone("zone2");
        assertThat(predicate.apply(predicateKey), is(false));
        assertThat(predicate.toString(), is("ZoneAffinityMatcher[zone=zone1]"));
    }

    @Test
    public void should_not_filter_when_server_zone_is_same() {
        server.setZone(expectedZone);
        assertThat(predicate.apply(predicateKey), is(true));
    }

}