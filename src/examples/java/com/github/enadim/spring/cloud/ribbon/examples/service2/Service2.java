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
package com.github.enadim.spring.cloud.ribbon.examples.service2;

import com.github.enadim.spring.cloud.ribbon.examples.ribbon.RibbonClientsFavoriteZoneConfig;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@SpringBootApplication
@EnableEurekaClient
@RibbonClients(defaultConfiguration = {RibbonClientsFavoriteZoneConfig.class})
@EnableContextPropagation
@EnableConfigurationProperties(EurekaInstanceProperties.class)
@RestController
@Slf4j
public class Service2 {
    @Autowired
    protected EurekaInstanceProperties eurekaInstanceMetadataProperties;

    @RequestMapping(method = GET, value = "/service2/message")
    @ResponseStatus(HttpStatus.OK)
    public String getMessage(@RequestParam(value = "useCase") String useCase) {
        log.info("use case: {}", useCase);
        return eurekaInstanceMetadataProperties.getInstanceId();
    }

    public static void main(String[] args) {
        SpringApplication.run(Service2.class, args);
    }
}
