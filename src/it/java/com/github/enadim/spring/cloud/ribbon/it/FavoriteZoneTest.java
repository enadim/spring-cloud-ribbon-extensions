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
import static org.springframework.http.HttpStatus.OK;

public class FavoriteZoneTest extends AbstractTest {

    public FavoriteZoneTest() {
        super("application1", "application12");
    }

    @Test
    public void favorite_zone_route_to_zone1() {
        given().log().uri().log().headers()
                .header("zone", "zone1")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("application12->application21"));
    }

    @Test
    public void favorite_zone_route_to_zone1_concurrent() {
        parallelRun(this::favorite_zone_route_to_zone1);
    }

    @Test
    public void favorite_zone_route_to_zone2() {
        given().log().uri().log().headers()
                .header("zone", "zone2")
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("application12->application22"));
    }

    @Test
    public void favorite_zone_route_to_zone2_concurrent() {
        parallelRun(this::favorite_zone_route_to_zone2);
    }

    @Test
    public void favorite_zone_route_to_same_zone() {
        given().log().uri().log().headers()
                .when()
                .get("/message")
                .then()
                .statusCode(OK.value())
                .body(is("application12->application22"));
    }

    @Test
    public void favorite_zone_route_to_same_zone_concurrent() {
        parallelRun(this::favorite_zone_route_to_same_zone);
    }
}