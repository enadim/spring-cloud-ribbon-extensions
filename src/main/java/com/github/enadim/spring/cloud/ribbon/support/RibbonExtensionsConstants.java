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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RibbonExtensionsConstants {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    public static final String DEFAULT_VALUE_SEPARATOR = ":";
    public static final String TRUE = "true";
    public static final String DOT = ".";
    public static final String ENABLED = "enabled";
    public static final String EXTENSION_PREFIX = "ribbon.extensions";
    public static final String RULE = "rule";
    public static final String RULE_PREFIX = EXTENSION_PREFIX + DOT + RULE;
    public static final String CLIENT_NAME = "${ribbon.client.name}";
    public static final String CLIENT_RULE_PREFIX = EXTENSION_PREFIX + ".client." + CLIENT_NAME + DOT + RULE;

    public static final String PROPAGATION_PREFIX = EXTENSION_PREFIX + DOT + "propagation";

    public static final String EUREKA_INSTANCE_ID = "instanceId";
    public static final String EUREKA_INSTANCE_PREFIX = "eureka.instance";
    public static final String EUREKA_ZONE_PROPERTY = "zone";
    public static final String DEFAULT_EUREKA_ZONE = "default";

    public static final String ANY_PREDICATE_DESCRIPTION = "any()";
    public static final String AVAILABILITY_PREDICATE_DESCRIPTION = "Availability";
    public static final String ZONE_AVOIDANCE_PREDICATE_DESCRIPTION = "ZoneAvoidance";

    public static final String FAVORITE_ZONE_RULE = "favorite-zone";
    public static final String FAVORITE_ZONE_PREFIX = RULE_PREFIX + DOT + FAVORITE_ZONE_RULE;
    public static final String FAVORITE_ZONE_RULE_ENABLED = FAVORITE_ZONE_PREFIX + DOT + ENABLED;
    public static final String FAVORITE_ZONE_RULE_CLIENT_ENABLED = CLIENT_RULE_PREFIX + DOT + FAVORITE_ZONE_RULE + DOT + ENABLED;
    public static final String FAVORITE_ZONE_RULE_CLIENT_ENABLED_EXPRESSION = DEFAULT_PLACEHOLDER_PREFIX + FAVORITE_ZONE_RULE_CLIENT_ENABLED + DEFAULT_VALUE_SEPARATOR + TRUE + DEFAULT_PLACEHOLDER_SUFFIX;
    public static final String DEFAULT_FAVORITE_ZONE_KEY = "favorite-zone";
    public static final String DEFAULT_UPSTREAM_ZONE_KEY = "upstream-zone";


    public static final String ZONE_AFFINITY_RULE = "zone-affinity";
    public static final String ZONE_AFFINITY_PREFIX = RULE_PREFIX + DOT + ZONE_AFFINITY_RULE;
    public static final String ZONE_AFFINITY_RULE_ENABLED = ZONE_AFFINITY_PREFIX + DOT + ENABLED;
    public static final String ZONE_AFFINITY_RULE_CLIENT_ENABLED = CLIENT_RULE_PREFIX + DOT + ZONE_AFFINITY_RULE + DOT + ENABLED;
    public static final String ZONE_AFFINITY_RULE_CLIENT_ENABLED_EXPRESSION = DEFAULT_PLACEHOLDER_PREFIX + ZONE_AFFINITY_RULE_CLIENT_ENABLED + DEFAULT_VALUE_SEPARATOR + TRUE + DEFAULT_PLACEHOLDER_SUFFIX;

    public static final String STRICT_METADATA_MATCHER_RULE = "strict-metadata-matcher";
    public static final String STRICT_METADATA_MATCHER_PREFIX = RULE_PREFIX + DOT + STRICT_METADATA_MATCHER_RULE;
    public static final String STRICT_METADATA_MATCHER_RULE_ENABLED = STRICT_METADATA_MATCHER_PREFIX + DOT + ENABLED;
    public static final String STRICT_METADATA_MATCHER_RULE_CLIENT_ENABLED = CLIENT_RULE_PREFIX + DOT + STRICT_METADATA_MATCHER_RULE + DOT + ENABLED;
    public static final String STRICT_METADATA_MATCHER_RULE_CLIENT_ENABLED_EXPRESSION = DEFAULT_PLACEHOLDER_PREFIX + STRICT_METADATA_MATCHER_RULE_CLIENT_ENABLED + DEFAULT_VALUE_SEPARATOR + TRUE + DEFAULT_PLACEHOLDER_SUFFIX;

    public static final String DYNAMIC_METADATA_MATCHER_RULE = "strict-metadata-matcher";
    public static final String DYNAMIC_METADATA_MATCHER_PREFIX = RULE_PREFIX + DOT + STRICT_METADATA_MATCHER_RULE;
    public static final String DYNAMIC_METADATA_MATCHER_RULE_ENABLED = DYNAMIC_METADATA_MATCHER_PREFIX + DOT + ENABLED;
    public static final String DYNAMIC_METADATA_MATCHER_RULE_CLIENT_ENABLED = CLIENT_RULE_PREFIX + DOT + DYNAMIC_METADATA_MATCHER_RULE + DOT + ENABLED;
    public static final String DYNAMIC_METADATA_MATCHER_RULE_CLIENT_ENABLED_EXPRESSION = DEFAULT_PLACEHOLDER_PREFIX + DYNAMIC_METADATA_MATCHER_RULE_CLIENT_ENABLED + DEFAULT_VALUE_SEPARATOR + TRUE + DEFAULT_PLACEHOLDER_SUFFIX;

}
