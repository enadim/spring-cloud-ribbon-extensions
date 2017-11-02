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
package com.github.enadim.spring.cloud.ribbon.support.favorite;

import com.github.enadim.spring.cloud.ribbon.support.AbstractSupportTest;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonFavoriteZone;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;

public abstract class AbstractFavoriteZoneSupportTest extends AbstractSupportTest {
    private final String favoriteZoneKey;

    public AbstractFavoriteZoneSupportTest(String favoriteZoneKey) {
        this.favoriteZoneKey = favoriteZoneKey;
    }

    @Test
    public void test_choose_server_in_zone1_using_favorite_zone_matcher() {
        given().when()
                .header(favoriteZoneKey, TestApplicationBase.TestControllerConstants.ZONE1)
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Test
    public void test_choose_server_in_zone2_using_favorite_zone_matcher() {
        given().when()
                .header(favoriteZoneKey, TestApplicationBase.TestControllerConstants.ZONE2)
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }


    @Test
    public void test_choose_server_in_zone1_using_zone_matcher() {
        given().when()
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Test
    public void test_choose_any_server_even_if_unavailable() {
        server1.setAlive(false);
        server2.setAlive(false);
        given().when()
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @SpringBootApplication
    @EnableAsync
    @EnableFeignClients(basePackageClasses = TestApplicationResource.class)
    @EnableRibbonContextPropagation
    @RibbonClients(defaultConfiguration = DefaultRibbonClientsConfig.class,
            value = {@RibbonClient(name = TestApplicationResource.SERVICE_ID, configuration = FavoriteZoneRibbonClientsConfig.class)})
    public static class FavoriteZoneApplication extends TestApplicationBase {
        @Bean
        public ExecutorService executorService() {
            return Executors.newSingleThreadExecutor();
        }
    }

    @Configuration
    @EnableRibbonFavoriteZone
    public static class FavoriteZoneRibbonClientsConfig {
    }

}