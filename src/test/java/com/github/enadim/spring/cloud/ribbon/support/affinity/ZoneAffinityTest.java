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
package com.github.enadim.spring.cloud.ribbon.support.affinity;

import com.github.enadim.spring.cloud.ribbon.propagator.concurrent.ContextAwareExecutorService;
import com.github.enadim.spring.cloud.ribbon.support.AbstractSupportTest;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EnableHttpLogging;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonZoneAffinity;
import com.github.enadim.spring.cloud.ribbon.support.affinity.ZoneAffinityTest.ZoneAffinityApplication;
import com.github.enadim.spring.cloud.ribbon.support.strategy.PreservesExecutionContextHystrixStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ZoneAffinityApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.application.name=zone-affinity-test",
                "endpoints.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.instance.metadataMap.zone=zone1",
                "ribbon.eager-load.enabled=true",
                "ribbon.eager-load.clients[0]=application2"}
)
public class ZoneAffinityTest extends AbstractSupportTest {

    static {
        PreservesExecutionContextHystrixStrategy.init();
    }

    @Test
    public void test_choose_server1() {
        given().when()
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Test
    public void test_choose_server2() {
        server1.setAlive(false);
        server1.setReadyToServe(false);
        given().when()
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @SpringBootApplication
    @EnableFeignClients(basePackageClasses = TestApplicationResource.class)
    @EnableContextPropagation
    @RibbonClients(defaultConfiguration = DefaultRibbonClientsConfig.class,
            value = {@RibbonClient(name = TestApplicationResource.SERVICE_ID, configuration = ZoneAffinityRibbonClientsConfig.class)})
    @EnableHttpLogging
    public static class ZoneAffinityApplication extends TestApplicationBase {
        @Bean
        public ExecutorService executorService() {
            return new ContextAwareExecutorService(Executors.newSingleThreadExecutor());
        }
    }

    @Configuration
    @EnableRibbonZoneAffinity
    public static class ZoneAffinityRibbonClientsConfig {
    }

}