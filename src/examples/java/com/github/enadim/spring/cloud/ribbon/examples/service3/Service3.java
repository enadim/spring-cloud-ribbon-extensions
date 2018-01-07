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
package com.github.enadim.spring.cloud.ribbon.examples.service3;

import com.github.enadim.spring.cloud.ribbon.examples.api.service2.Service2Resource;
import com.github.enadim.spring.cloud.ribbon.examples.ribbon.RibbonClientsStrictMetadataMatcherConfig;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import com.github.enadim.spring.cloud.ribbon.support.EnableHttpLogging;
import com.github.enadim.spring.cloud.ribbon.support.EurekaInstanceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@SpringBootApplication
@EnableEurekaClient
@EnableContextPropagation
@RibbonClients(defaultConfiguration = RibbonClientsStrictMetadataMatcherConfig.class)
@EnableFeignClients(basePackageClasses = Service2Resource.class)
@EnableConfigurationProperties(EurekaInstanceProperties.class)
@RestController
@EnableHttpLogging
public class Service3 {
    @Inject
    protected EurekaInstanceProperties eurekaInstanceMetadataProperties;

    @Inject
    Service2Resource service2;

    @RequestMapping(method = GET, value = "/service3/message")
    @ResponseStatus(HttpStatus.OK)
    public String getMessage(@RequestParam(value = "useCase") String useCase) {
        return format("%s->%s", eurekaInstanceMetadataProperties.getInstanceId(), service2.getMessage(useCase));
    }

    public static void main(String[] args) {
        SpringApplication.run(Service3.class, args);
    }
}
