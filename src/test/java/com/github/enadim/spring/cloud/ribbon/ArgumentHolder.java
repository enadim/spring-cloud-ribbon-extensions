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
package com.github.enadim.spring.cloud.ribbon;

import lombok.Getter;
import org.mockito.ArgumentMatcher;

import java.io.Serializable;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

public class ArgumentHolder<T> implements ArgumentMatcher<T>, Serializable {
    @Getter
    T argument;

    @Override
    public boolean matches(T argument) {
        this.argument = argument;
        return true;
    }

    public T eq() {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(this);
        return argument;
    }
}
