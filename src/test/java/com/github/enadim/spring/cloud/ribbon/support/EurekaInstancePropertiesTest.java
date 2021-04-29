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
package com.github.enadim.spring.cloud.ribbon.support;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class EurekaInstancePropertiesTest {
    EurekaInstanceProperties properties = new EurekaInstanceProperties();

    @Test
    public void metadata() throws Exception {
        assertThat(properties.getMetadataMap().get("key")).isNull();
        properties.setMetadataMap(new HashMap<String, String>() {
            {
                put("key", "value");
            }
        });
        assertThat(properties.getMetadataMap().get("key")).isEqualTo("value");
    }

    @Test
    public void getInstanceId() throws Exception {
        assertThat(properties.getInstanceId()).isNull();
        properties.getMetadataMap().put("instanceId", "instanceId");
        assertThat(properties.getInstanceId()).isEqualTo("instanceId");
    }

    @Test
    public void getZone() throws Exception {
        assertThat(properties.getZone()).isEqualTo("default");
        properties.getMetadataMap().put("zone", "zone");
        assertThat(properties.getZone()).isEqualTo("zone");
    }


}