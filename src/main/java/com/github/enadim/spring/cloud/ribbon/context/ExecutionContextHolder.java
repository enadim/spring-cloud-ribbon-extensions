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
package com.github.enadim.spring.cloud.ribbon.context;

/**
 * Execution CONTEXT holder.
 *
 * @author Nadim Benabdenbi
 */
public final class ExecutionContextHolder {


    /**
     * Stores the {@link ExecutionContext} for current thread.
     */
    private static final ThreadLocal<ExecutionContext> CONTEXT = new InheritableThreadLocal<ExecutionContext>() {
        @Override
        protected ExecutionContext initialValue() {
            return new DefaultExecutionContext();
        }
    };


    /**
     * utility class should not be instantiated
     */
    private ExecutionContextHolder() {
    }

    /**
     * Retrieves the current CONTEXT.
     *
     * @return the current CONTEXT.
     */
    public static ExecutionContext current() {
        return CONTEXT.get();
    }

    /**
     * switches the current CONTEXT to the provided one.
     *
     * @param context the current CONTEXT replacement.
     * @return the current CONTEXT.
     */
    public static ExecutionContext switchTo(ExecutionContext context) {
        ExecutionContextHolder.CONTEXT.set(context);
        return context;
    }

    /**
     * removes the current CONTEXT.
     *
     * @return the CONTEXT that have been removed.
     */
    public static ExecutionContext remove() {
        ExecutionContext current = CONTEXT.get();
        CONTEXT.remove();
        return current;
    }
}
