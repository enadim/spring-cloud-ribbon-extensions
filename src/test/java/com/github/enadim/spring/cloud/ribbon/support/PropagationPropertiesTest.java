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

import static org.assertj.core.api.Assertions.assertThat;

public class PropagationPropertiesTest {


    @Test
    public void buildEntriesFilter() throws Exception {
        PropagationProperties properties = new PropagationProperties();
        assertThat(properties.buildEntriesFilter().accept("")).isFalse();
    }

    @Test
    public void buildExtraStaticEntries() throws Exception {
        PropagationProperties properties = new PropagationProperties();
        assertThat(properties.buildExtraStaticEntries(new EurekaInstanceProperties()).get("upstream-zone")).isEqualTo("default");

        properties = new PropagationProperties();
        properties.getUpStreamZone().setKey("new");
        assertThat(properties.buildExtraStaticEntries(new EurekaInstanceProperties()).get("new")).isEqualTo("default");

        properties = new PropagationProperties();
        properties.getUpStreamZone().setEnabled(false);
        assertThat(properties.buildExtraStaticEntries(new EurekaInstanceProperties()).get("upstream-zone")).isNull();
    }

}