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
package com.github.enadim.spring.cloud.ribbon.support.dynamic;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AbstractDynamicMatcherSupportTest.DynamicMatcherApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.application.name=dynamic-matcher-test",
                "endpoints.enabled=false",
                "eureka.client.enabled=false",
                "ribbon.eager-load.enabled=true",
                "ribbon.eager-load.clients[0]=application2",
                "ribbon.extensions.propagation.keys[0]=dynamic-matcher-key",
                "ribbon.extensions.propagation.keys[1]=instance-id"}
)
public class DynamicMatcherDefaultConfigurationTest extends AbstractDynamicMatcherSupportTest {
    public DynamicMatcherDefaultConfigurationTest() {
        super("dynamic-matcher-key", true);
    }

}