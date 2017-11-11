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

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

public class StrictMatcherTest extends AbstractTest {
    public StrictMatcherTest() {
        super("service3", "service3-zone1");
    }

    @Test
    public void should_choose_any() {
        given().log().uri().log().headers()
                .param("useCase", "should_choose_any")
                .when()
                .get("message")
                .then()
                .statusCode(OK.value())
                .body(startsWith("service3-zone1->service2-zone"));
    }

    @Test
    public void should_choose_any_concurrent() {
        parallelRun(this::should_choose_any);
    }

    @Test
    public void should_choose_service2_zone1() {
        given().log().uri().log().headers()
                .header("version", "1.0.0")
                .param("useCase", "should_choose_service2_zone1")
                .when()
                .get("message")
                .then()
                .statusCode(OK.value())
                .body(is("service3-zone1->service2-zone1"));
    }

    @Test
    public void should_choose_service2_zone1_concurrent() {
        parallelRun(this::should_choose_service2_zone1);
    }

    @Test
    public void should_choose_service2_zone2() {
        given().log().uri().log().headers()
                .header("version", "2.0.0")
                .param("useCase", "should_choose_service2_zone2")
                .when()
                .get("message")
                .then()
                .statusCode(OK.value())
                .body(is("service3-zone1->service2-zone2"));
    }

    @Test
    public void should_choose_service2_zone2_concurrent() {
        parallelRun(this::should_choose_service2_zone2);
    }

    @Test
    public void should_fail_no_available_server() {
        given().log().uri().log().headers()
                .header("version", "9.9.9")
                .param("useCase", "should_fail_no_available_server")
                .when()
                .get("message")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void should_fail_no_available_concurrent() {
        parallelRun(this::should_fail_no_available_server);
    }
}