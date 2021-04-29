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

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpStatus.OK;

public class FavoriteZoneTest extends AbstractTest {

    public FavoriteZoneTest() {
        super("service1", "service1-zone1");
        //parrallelRunEnabled = false;
    }

    @Test
    public void should_choose_zone1_when_no_zone_is_requested() {
        given().log().uri().log().headers()
                .body("should_choose_zone1_when_no_zone_is_requested")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("service1-zone1->service2-zone1"));
    }

    @Test
    public void should_choose_zone1_when_no_zone_is_requested_concurrent() {
        parallelRun(this::should_choose_zone1_when_no_zone_is_requested);
    }

    @Test
    public void should_choose_any_zone_when_unknown_zone_is_requested() {
        given().log().uri().log().headers()
                .header("favorite-zone", "zone99")
                .body("should_choose_any_zone_when_unknown_zone_is_requested")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(startsWith("service1-zone1->service2-zone"));
    }

    @Test
    public void should_choose_any_zone_when_unknown_zone_is_requested_concurrent() {
        parallelRun(this::should_choose_any_zone_when_unknown_zone_is_requested);
    }

    @Test
    public void should_choose_zone1_when_zone1_is_requested() {
        given().log().uri().log().headers()
                .header("favorite-zone", "zone1")
                .body("should_choose_zone1_when_zone1_is_requested")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("service1-zone1->service2-zone1"));
    }

    @Test
    public void should_choose_zone1_when_zone1_is_requested_concurrent() {
        parallelRun(this::should_choose_zone1_when_zone1_is_requested);
    }

    @Test
    public void should_choose_zone2_when_zone2_is_requested() {
        given().log().uri().log().headers()
                .header("favorite-zone", "zone2")
                .body("should_choose_zone2_when_zone2_is_requested")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("service1-zone1->service2-zone2"));
    }

    @Test
    public void should_choose_zone2_when_zone2_is_requested_concurrent() {
        parallelRun(this::should_choose_zone2_when_zone2_is_requested);
    }


}