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
package com.github.enadim.spring.cloud.ribbon.support;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractSupportTest {

    @LocalServerPort
    int port;

    @Inject
    @Named("server1")
    protected DiscoveryEnabledServer server1;
    @Inject
    @Named("server2")
    protected DiscoveryEnabledServer server2;

    static {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = TestApplicationBase.TestControllerConstants.BASE_PATH;
    }

    @Before
    public void before() {
        RestAssured.port = port;
        server1.setPort(port);
        server2.setPort(port);
    }

    public void reset(DiscoveryEnabledServer server) {
        server.setAlive(true);
        server.setReadyToServe(true);
        server.getInstanceInfo().getMetadata().clear();
    }

    @After
    public void after() {
        reset(server1);
        reset(server2);
    }

    @Test
    public void test_default() {
        given().when()
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Configuration
    public static class TestApplicationBase {

        public interface TestControllerConstants {
            String BASE_PATH = "application";
            String HELLO_PATH = "hello";
            String HELLO_MESSAGE = "Hello";
            String WORLD_MESSAGE = "World!";
            String WORLD_PATH = "world";
            String WORLD_FULL_PATH = BASE_PATH + "/" + WORLD_PATH;
            String MESSAGE_PATH = "message";
            String MESSAGE = HELLO_MESSAGE + " " + WORLD_MESSAGE;
            String ZONE1 = "zone1";
            String ZONE2 = "zone2";
            String INSTANCE_ID = "instance-id";
            String INSTANCE_ID1 = "InstanceId1";
            String INSTANCE_ID2 = "InstanceId2";
        }

        public DiscoveryEnabledServer createServer(String zone) {
            DiscoveryEnabledServer server = new DiscoveryEnabledServer(InstanceInfo.Builder.newBuilder()
                    .setAppName(TestApplicationResource.SERVICE_ID)
                    .setHostName("127.0.0.1")
                    .setMetadata(new HashMap<>())
                    .build(), false);
            server.setZone(zone);
            server.setAlive(true);
            server.setReadyToServe(true);
            return server;
        }

        @Bean("server1")
        public DiscoveryEnabledServer server1() {
            return createServer(TestControllerConstants.ZONE1);
        }

        @Bean("server2")
        public DiscoveryEnabledServer server2() {
            return createServer(TestControllerConstants.ZONE2);
        }

        @Bean("servers")
        public List<Server> servers() {
            return Arrays.asList(server1(), server2());
        }

        @Bean
        public ServerList<Server> serverList() {
            return new ServerList<Server>() {
                @Override
                public List<Server> getInitialListOfServers() {
                    return servers();
                }

                @Override
                public List<Server> getUpdatedListOfServers() {
                    return servers();
                }
            };
        }

        @RestController
        @RequestMapping(TestControllerConstants.BASE_PATH)
        @Slf4j
        public static class TestController {
            @Inject
            private TestApplicationResource testApplicationResource;

            @Inject
            private ExecutorService executorService;

            @RequestMapping(method = GET, value = TestControllerConstants.MESSAGE_PATH)
            public String getMessage() {
                try {
                    return format("%s %s", TestControllerConstants.HELLO_MESSAGE, executorService.submit(() -> testApplicationResource.getWorldMessage()).get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @RequestMapping(method = GET, value = TestControllerConstants.HELLO_PATH)
            public String getHelloMessage() {
                return TestControllerConstants.HELLO_MESSAGE;
            }

            @RequestMapping(method = GET, value = TestControllerConstants.WORLD_PATH)
            public String getWorldMessage() {
                return TestControllerConstants.WORLD_MESSAGE;
            }
        }
    }

    @FeignClient(TestApplicationResource.SERVICE_ID)
    public interface TestApplicationResource {
        String SERVICE_ID = "application";

        @RequestMapping(value = TestApplicationBase.TestControllerConstants.WORLD_FULL_PATH, method = RequestMethod.GET)
        @ResponseStatus(HttpStatus.OK)
        String getWorldMessage();
    }


    @Configuration
    public static class DefaultRibbonClientsConfig {
    }
}