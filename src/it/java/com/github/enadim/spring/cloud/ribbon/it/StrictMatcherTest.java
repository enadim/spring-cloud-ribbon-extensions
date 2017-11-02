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
        super("application1", "application11");
    }

    @Test
    public void strict_matcher_empty_attributes_route_found() {
        given().log().uri().log().headers()
                .when()
                .get("message")
                .then()
                .statusCode(OK.value())
                .body(startsWith("application11->application2"));
    }

    @Test
    public void strict_matcher_empty_attributes_route_found_concurrent() {
        parallelRun(this::strict_matcher_empty_attributes_route_found);
    }


    @Test
    public void strict_matcher_route_found() {
        given().log().uri().log().headers()
                .header("zone", "zone1")
                .header("version", "1.0.0")
                .when()
                .get("message")
                .then()
                .statusCode(OK.value())
                .body(is("application11->application21"));
    }

    @Test
    public void strict_matcher_route_found_concurrent() {
        parallelRun(this::strict_matcher_route_found);
    }

    @Test
    public void strict_matcher_route_not_found() {
        given().log().uri().log().headers()
                .header("zone", "zone1")
                .header("version", "2.0.0")
                .when()
                .get("message")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void strict_matcher_route_not_found_concurrent() {
        parallelRun(this::strict_matcher_route_not_found);
    }
}