/**
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
package com.github.enadim.spring.cloud.ribbon.api;

import org.junit.After;
import org.junit.Test;

import static org.mockito.Mockito.mock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.current;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.remove;
import static com.github.enadim.spring.cloud.ribbon.api.RibbonRuleContextHolder.switchTo;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class RibbonRuleContextHolderTest {

    @Test
    public void testCurrent() throws Exception {
        RibbonRuleContext context = current();
        assertThat(context.getAttributes().size(), is(0));
        remove();
    }

    @Test
    public void testSwitchTo() throws Exception {
        RibbonRuleContext context = mock(RibbonRuleContext.class);
        switchTo(context);
        assertThat(current(), is(context));
        remove();
    }

    @Test
    public void inheritance_works_when_creating_child_threads() throws Exception {
        final String key   = "key";
        final String value = "value";
        current().put(key, value);
        final ExecutorService executorService = newFixedThreadPool(1);
        final Future<String>  future          = executorService.submit(() -> current().get(key));
        assertThat(future.get(), is(value));
    }

    @Test
    public void inheritance_do_not_work_when_executor_created_elsewhere() throws Exception {
        //init executor : what will happen with a spring context creating its own executors
        final ExecutorService executorService = newFixedThreadPool(1);
        executorService.submit(() -> null).get();
        //test inheritance failure
        final String key   = "key";
        final String value = "value";
        current().put(key, value);
        final Future<String> future = executorService.submit(() -> current().get(key));
        assertThat(future.get(), is(nullValue()));
    }

    @After
    public void after() {
        remove();
    }

}
