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
package com.github.enadim.spring.cloud.ribbon.examples.application1;

import com.github.enadim.spring.cloud.ribbon.examples.api.application2.Application2Resource;
import com.github.enadim.spring.cloud.ribbon.examples.ribbon.RibbonClientsConfig;
import com.github.enadim.spring.cloud.ribbon.support.EnableExecutionContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@SpringBootApplication
@EnableConfigurationProperties(EurekaInstanceProperties.class)
@EnableEurekaClient
@EnableExecutionContextPropagation
@RibbonClients(defaultConfiguration = RibbonClientsConfig.class)
@EnableFeignClients(basePackageClasses = Application2Resource.class)
public class Application1 {
    @Inject
    protected EurekaInstanceProperties eurekaInstanceMetadataProperties;

    @Inject
    Application2Resource application2Resource;

    @RequestMapping(method = GET, value = "/application1/message")
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.ok().body(format("%s->%s", eurekaInstanceMetadataProperties.getInstanceId(), application2Resource.getMessage()));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application1.class, args);
    }
}
