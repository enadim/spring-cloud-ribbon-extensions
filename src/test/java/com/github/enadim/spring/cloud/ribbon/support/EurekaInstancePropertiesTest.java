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

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EurekaInstancePropertiesTest {
    EurekaInstanceProperties properties = new EurekaInstanceProperties();

    @Test
    public void metadata() throws Exception {
        assertNull(properties.getMetadataMap().get("key"));
        properties.setMetadataMap(new HashMap<String, String>() {
            {
                put("key", "value");
            }
        });
        assertEquals("value", properties.getMetadataMap().get("key"));
    }

    @Test
    public void getInstanceId() throws Exception {
        assertNull(properties.getInstanceId());
        properties.getMetadataMap().put("instanceId", "instanceId");
        assertEquals("instanceId", properties.getInstanceId());
    }

    @Test
    public void getZone() throws Exception {
        assertEquals("default", properties.getZone());
        properties.getMetadataMap().put("zone", "zone");
        assertEquals("zone", properties.getZone());
    }


}