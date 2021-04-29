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
package com.github.enadim.spring.cloud.ribbon.propagator.concurrent;

import org.junit.jupiter.api.AfterEach;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static org.mockito.Mockito.mock;

public abstract class AbstractExecutionContextAwareExecutorTest {
    protected final String key = "key";
    protected final String value = "value";
    protected final BlockingQueue<String> signal = new ArrayBlockingQueue<>(10);
    protected final Runnable runnable = () -> {
        try {
            signal.put(current().get(key));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };
    protected final Callable<String> callable = () -> {
        try {
            signal.put(current().get(key));
            return current().get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };
    protected final long period = 1;
    protected final Date date = new Date();
    protected final Trigger trigger = mock(Trigger.class);

    @AfterEach
    public void after() {
        remove();
    }

    public static <T> T uncheck(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}