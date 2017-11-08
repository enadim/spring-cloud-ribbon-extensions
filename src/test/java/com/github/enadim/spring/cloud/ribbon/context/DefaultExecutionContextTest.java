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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DefaultExecutionContextTest {
    final DefaultExecutionContext context = new DefaultExecutionContext();
    final String key1 = "key1";
    final String value1 = "value1";
    final String key2 = "key2";
    final String value2 = "value2";

    @Test
    public void testPut() {
        assertThat(context.put(key1, value1), is(context));
        assertThat(context.get(key1), is(value1));
        assertThat(context.put(key1, value2), is(context));
        assertThat(context.get(key1), is(value2));
    }

    @Test
    public void testPutIfAbsent() {
        assertThat(context.putIfAbsent(key1, value1), is(context));
        assertThat(context.get(key1), is(value1));
        assertThat(context.putIfAbsent(key1, value2), is(context));
        assertThat(context.get(key1), is(value1));
    }

    @Test
    public void testContainsKey() {
        assertThat(context.containsKey(key1), is(false));
        context.put(key1, value1);
        assertThat(context.containsKey(key1), is(true));
    }

    @Test
    public void testRemove() {
        assertThat(context.remove(key1), is(context));
        assertThat(context.containsKey(key1), is(false));
        context.put(key1, value1);
        assertThat(context.containsKey(key1), is(true));
        assertThat(context.remove(key1), is(context));
        assertThat(context.containsKey(key1), is(false));
    }

    @Test
    public void testCopy() throws CloneNotSupportedException {
        assertThat(context.put(key1, value1), is(context));
        assertThat(context.copy().get(key1), is(value1));
    }


    @Test
    public void enableConcurrency() throws Exception {
        assertThat(context.enableConcurrency(), is(context));
        assertThat(context.enableConcurrency(), is(context));
        testConcurrency();
    }

    public void testConcurrency() throws Exception {
        int concurrent = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrent);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String key = Integer.toString(i);
            context.put(key, key);
        }
        List<Future<Boolean>> futures = new ArrayList<>(concurrent);
        for (int i = 0; i < concurrent; i++) {
            futures.add(executorService.submit(() -> {
                List<Entry<String, String>> shuffles = new ArrayList<>(context.entrySet());
                Collections.shuffle(shuffles);
                shuffles.stream()
                        .forEach(x -> {
                            if (random.nextBoolean()) {
                                context.put(x.getKey(), x.getValue());
                            } else {
                                context.remove(x.getKey());
                            }
                        });
                return true;
            }));
        }
        for (Future<Boolean> x : futures) {
            assertThat(x.get(), is(true));
        }

    }
}