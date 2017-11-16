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
import org.junit.After;
import org.junit.Test;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class DynamicZoneMatcherTest {
    String favoriteZoneName = "favorite-zone";
    DynamicZoneMatcher predicate = new DynamicZoneMatcher(favoriteZoneName);
    Server server = new Server("id");
    PredicateKey predicateKey = new PredicateKey(server);

    @After
    public void after() {
        remove();
    }

    @Test
    public void should_filter_when_favorite_zone_not_provided() {
        assertThat(predicate.apply(predicateKey), is(false));
        assertThat(predicate.toString(), is("DynamicZoneMatcher[favorite-zone=null]"));
    }

    @Test
    public void should_filter_when_server_and_context_favorite_zones_are_different() {
        current().put(favoriteZoneName, "2");
        server.setZone("1");
        assertThat(predicate.apply(predicateKey), is(false));
        assertThat(predicate.toString(), is("DynamicZoneMatcher[favorite-zone=2]"));
    }

    @Test
    public void should_filter_when_server_and_context_favorite_zones_are_equals() {
        current().put(favoriteZoneName, "1");
        server.setZone("1");
        assertThat(predicate.apply(predicateKey), is(true));
    }

}