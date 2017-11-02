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
package com.github.enadim.spring.cloud.ribbon.it;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;

public abstract class AbstractTest {
    protected int maxConcurrentTasks = 10;
    protected ExecutorService executorService = Executors.newFixedThreadPool(maxConcurrentTasks * 2);
    private final String basePath;
    private final String applicationName;

    public AbstractTest(String basePath, String applicationName) {
        this.basePath = basePath;
        this.applicationName = applicationName;
    }

    @Before
    @HystrixCommand
    public final void abstractBefore() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "";
        /*Arrays.asList("application21", "application22", "application11", "application12").forEach(x -> {
                    RestAssured.port = getApplicationPort(x);
                    given().when()
                            .get("message")
                            .then()
                            .statusCode(any(Integer.class));
                }
        );*/
        RestAssured.port = getApplicationPort(applicationName);
        RestAssured.basePath = basePath;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        given().when()
                .get("message")
                .then()
                .statusCode(any(Integer.class));
    }

    private int getApplicationPort(String name) {
        switch (name) {
            case "eureka":
                return 8000;
            case "zuul":
                return 8001;
            case "application11":
                return 8011;
            case "application12":
                return 8012;
            case "application21":
                return 8021;
            case "application22":
                return 8022;
            default:
                throw new IllegalArgumentException(name);
        }
    }

    protected void parallelRun(Runnable r) {
        AtomicInteger errors = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>(maxConcurrentTasks);
        for (int i = 0; i < maxConcurrentTasks; i++) {
            futures.add(
                    executorService.submit(() -> {
                        try {
                            r.run();
                        } catch (AssertionError e) {
                            errors.incrementAndGet();
                        }
                    }));
        }
        futures.forEach(x -> {
            try {
                x.get();
            } catch (Exception e) {
                errors.incrementAndGet();
            }
        });
        if (errors.get() > 0) {
            throw new IllegalStateException();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void parallelRunTest() {
        parallelRun(Assert::fail);
    }
}
