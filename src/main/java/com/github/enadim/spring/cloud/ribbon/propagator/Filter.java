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
package com.github.enadim.spring.cloud.ribbon.propagator;

/**
 * An interface that decide if an object should be accepted or filtered.
 *
 * @param <T> the type of the filter target object.
 * @author Nadim Benabdenbi
 */
@FunctionalInterface
public interface Filter<T> {

    /**
     * Decides if the given object should be accepted or filtered..
     *
     * @param t the object to evaluate
     * @return {@code true} if the object should be accepted otherwise {@code false}.
     */
    boolean accept(T t);
}
