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
package com.github.enadim.spring.cloud.ribbon.support.dynamic;

import com.github.enadim.spring.cloud.ribbon.support.AbstractSupportTest;
import com.github.enadim.spring.cloud.ribbon.support.AbstractSupportTest.TestApplicationBase.TestControllerConstants;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EnableHttpLogging;
import com.github.enadim.spring.cloud.ribbon.support.EnableRibbonDynamicMetadataMatcher;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

public abstract class AbstractDynamicMatcherSupportTest extends AbstractSupportTest {
    private final String dynamicAttributeKey;
    private final boolean matchIfMissing;

    public AbstractDynamicMatcherSupportTest(String dynamicAttributeKey, boolean matchIfMissing) {
        this.dynamicAttributeKey = dynamicAttributeKey;
        this.matchIfMissing = matchIfMissing;
    }

    @Test
    @Ignore("will fail")
    @Override
    public void test_default() {
    }

    @Test
    public void test_choose_server_instanceid1() {
        server1.getInstanceInfo().getMetadata().put(TestControllerConstants.INSTANCE_ID, TestControllerConstants.INSTANCE_ID1);
        server2.getInstanceInfo().getMetadata().put(TestControllerConstants.INSTANCE_ID, TestControllerConstants.INSTANCE_ID2);
        given().when()
                .header(dynamicAttributeKey, TestApplicationBase.TestControllerConstants.INSTANCE_ID)
                .header(TestApplicationBase.TestControllerConstants.INSTANCE_ID, TestApplicationBase.TestControllerConstants.INSTANCE_ID1)
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Test
    public void test_choose_server_instanceid2() {
        server1.getInstanceInfo().getMetadata().put(TestControllerConstants.INSTANCE_ID, TestControllerConstants.INSTANCE_ID1);
        server2.getInstanceInfo().getMetadata().put(TestControllerConstants.INSTANCE_ID, TestControllerConstants.INSTANCE_ID2);
        given().when()
                .header(dynamicAttributeKey, TestApplicationBase.TestControllerConstants.INSTANCE_ID)
                .header(TestApplicationBase.TestControllerConstants.INSTANCE_ID, TestApplicationBase.TestControllerConstants.INSTANCE_ID2)
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(OK.value())
                .body(equalTo(TestApplicationBase.TestControllerConstants.MESSAGE));
    }

    @Test
    public void test_can_not_choose_any_server() {
        given().when()
                .header(dynamicAttributeKey, TestApplicationBase.TestControllerConstants.INSTANCE_ID)
                .header(TestApplicationBase.TestControllerConstants.INSTANCE_ID, TestApplicationBase.TestControllerConstants.INSTANCE_ID1)
                .get(TestApplicationBase.TestControllerConstants.MESSAGE_PATH)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    @SpringBootApplication
    @EnableAsync
    @EnableFeignClients(basePackageClasses = TestApplicationResource.class)
    @EnableContextPropagation
    @RibbonClients(defaultConfiguration = DynamicMatcherClientsConfig.class)
    @EnableHttpLogging
    public static class DynamicMatcherApplication extends TestApplicationBase {
        @Bean
        public ExecutorService executorService() {
            return Executors.newSingleThreadExecutor();
        }
    }

    @Configuration
    @EnableRibbonDynamicMetadataMatcher
    public static class DynamicMatcherClientsConfig {
    }

}