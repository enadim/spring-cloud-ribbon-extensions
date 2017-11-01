/**
 * Copyright (c) 2017 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.support;


import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables {@link RibbonRuleContext} attributes propagation.
 * <p>To be used at spring boot configuration level. For example:
 * <blockquote><pre>
 * &#064;EnableRibbonContextPropagation
 * &#064;SpringBootApplication
 * public class Application{
 *  ...
 * }
 * </pre></blockquote>
 *
 * @author Nadim Benabdenbi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ContextPropagationConfig.class)
public @interface EnableRibbonContextPropagation {
}
