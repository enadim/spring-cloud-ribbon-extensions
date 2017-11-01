/**
 * Copyright (c) 2015 the original author or authors
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
package com.github.enadim.spring.cloud.ribbon.api;

/**
 * The Ribbon rule context holder.
 *
 * @author Nadim Benabdenbi
 */
public interface RibbonRuleContextHolder {

    /**
     * Stores the {@link RibbonRuleContext} for current thread.
     */
    ThreadLocal<RibbonRuleContext> contextHolder = new InheritableThreadLocal<RibbonRuleContext>() {
        @Override
        protected RibbonRuleContext initialValue() {
            return new DefaultRibbonRuleContext();
        }
    };

    /**
     * Retrieves the current context
     *
     * @return the current context
     */
    static RibbonRuleContext current() {
        return contextHolder.get();
    }

    /**
     * switches the current context to the provided one
     *
     * @param context the current context replacement
     * @return the current context
     */
    static RibbonRuleContext switchTo(RibbonRuleContext context) {
        contextHolder.set(context);
        return context;
    }

    /**
     * removes the current context.
     *
     * @return the context before removal
     */
    static RibbonRuleContext remove() {
        final RibbonRuleContext current = contextHolder.get();
        contextHolder.remove();
        return current;
    }
}
