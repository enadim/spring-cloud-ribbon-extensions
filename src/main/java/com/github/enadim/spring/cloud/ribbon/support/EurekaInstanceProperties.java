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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Eureka instance propagationProperties.
 *
 * @author Nadim Benabdenbi
 */
@ConfigurationProperties(prefix = "eureka.instance")
@Component
@Getter
@Setter
public class EurekaInstanceProperties {
    /**
     * the eureka client meta data
     */
    private Map<String, String> metadataMap = new HashMap<>();

    /**
     * @return the eureka client instance id
     */
    public String getInstanceId() {
        return metadataMap.get("instanceId");
    }

    /**
     * @return the eureka client zone
     */
    public String getZone() {
        return metadataMap.getOrDefault("zone", "default");
    }
}
