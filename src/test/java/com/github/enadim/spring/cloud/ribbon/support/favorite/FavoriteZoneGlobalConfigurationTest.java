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
package com.github.enadim.spring.cloud.ribbon.support.favorite;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AbstractFavoriteZoneSupportTest.FavoriteZoneApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.application.name=favorite-zone-test",
                "endpoints.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.instance.metadataMap.zone=zone1",
                "ribbon.eager-load.enabled=true",
                "ribbon.eager-load.clients[0]=application2",
                "ribbon.extensions.propagation.executor.excludes[0]=taskScheduler",
                "ribbon.extensions.propagation.keys[0]=my-favorite-zone",
                "ribbon.extensions.client.application.rule.favorite-zone.key=my-favorite-zone"}
)
public class FavoriteZoneGlobalConfigurationTest extends AbstractFavoriteZoneSupportTest {

    public FavoriteZoneGlobalConfigurationTest() {
        super("favorite-zone");
    }
}