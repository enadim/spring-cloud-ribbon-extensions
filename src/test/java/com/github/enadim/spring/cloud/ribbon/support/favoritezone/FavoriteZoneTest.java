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
package com.github.enadim.spring.cloud.ribbon.support.favoritezone;

import com.github.enadim.spring.cloud.ribbon.it.api.application2.Application2Resource;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonFavoriteZone;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {FavoriteZoneTest.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class FavoriteZoneTest {

    @LocalServerPort
    int port;

    @Test
    public void test() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "";
        RestAssured.port = port;
        given().when()
                .get("/")
                .then()
                .statusCode(any(Integer.class));

    }

    @SpringBootApplication
    @RibbonClients(defaultConfiguration = RibbonClientsConfig.class)
    @EnableFeignClients(basePackageClasses = Application2Resource.class)
    @RestController
    public static class Application {
        @Inject
        Application2Resource application2Resource;

        @RequestMapping(method = GET)
        public String getMessage() {
            return application2Resource.getMessage();
        }
    }

    @Configuration
    @EnableRibbonFavoriteZone
    public static class RibbonClientsConfig {
    }
}