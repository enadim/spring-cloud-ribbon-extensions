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


import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables zone affinity load balancing rule.
 * <p>To be used at ribbon configuration level. For example:
 * <blockquote><pre>
 * &#064;RibbonClients(defaultConfiguration = RibbonClientsConfig.class)
 * &#064;SpringBootApplication
 * public class Application{
 *  ...
 * }
 * &#064;Configuration
 * &#064;EnableRibbonZoneAffinity
 * public class RibbonClientsConfig {
 * }
 * </pre></blockquote>
 *
 * @author Nadim Benabdenbi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ZoneAffinityConfig.class)
public @interface EnableRibbonZoneAffinity {
}
