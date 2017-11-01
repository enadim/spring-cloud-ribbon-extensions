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
package com.github.enadim.spring.cloud.ribbon.propagator;

import com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContext;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.switchTo;

/**
 * {@link RibbonRuleContext} Propagator for {@link Runnable}.
 *
 * @author Nadim Benabdenbi
 */
public class PropagationRunnable implements Runnable {
    final Runnable delegate;
    final RibbonRuleContext context;

    PropagationRunnable(Runnable delegate) {
        this.delegate = delegate;
        context = current().enableConcurrency();
    }

    @Override
    public void run() {
        switchTo(context);
        delegate.run();
    }

    /**
     * Wraps a Runnable to a {@link PropagationRunnable}
     *
     * @param runnable the runnable to wrap
     * @return the instance of {@link PropagationRunnable} over the runnable
     */
    public static Runnable wrap(Runnable runnable) {
        return new PropagationRunnable(runnable);
    }
}
