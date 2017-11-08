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

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.remove;
import static com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder.switchTo;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

public class ExecutionContextHolderTest {

    @Test
    public void testCurrent() throws Exception {
        ExecutionContext context = current();
        assertThat(context.entrySet().size(), is(0));
        remove();
    }

    @Test
    public void testSwitchTo() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        switchTo(context);
        assertThat(current(), is(context));
        remove();
    }

    @Test
    public void inheritance_works_when_creating_child_threads() throws Exception {
        String key = "key";
        String value = "value";
        current().put(key, value);
        ExecutorService executorService = newFixedThreadPool(1);
        Future<String> future = executorService.submit(() -> current().get(key));
        assertThat(future.get(), is(value));
    }

    @Test
    public void inheritance_do_not_work_when_executor_created_elsewhere() throws Exception {
        //init executor : what will happen with a spring context creating its own executors
        ExecutorService executorService = newFixedThreadPool(1);
        executorService.submit(() -> null).get();
        //test inheritance failure
        String key = "key";
        String value = "value";
        current().put(key, value);
        Future<String> future = executorService.submit(() -> current().get(key));
        assertThat(future.get(), is(nullValue()));
    }

    @After
    public void after() {
        remove();
    }

}
