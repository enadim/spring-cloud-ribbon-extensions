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
package com.github.enadim.spring.cloud.ribbon.examples.jms;

import com.github.enadim.spring.cloud.ribbon.context.ExecutionContext;
import com.github.enadim.spring.cloud.ribbon.context.ExecutionContextHolder;
import com.github.enadim.spring.cloud.ribbon.support.EnableContextPropagation;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;

@SpringBootApplication
@EnableContextPropagation
public class JmsTest implements ApplicationRunner {
    @Inject
    JmsTemplate jmsTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExecutionContextHolder.current().put("favorite-zone", "zone1");
        String expected = "message";
        jmsTemplate.convertAndSend("queue", expected);
        jmsTemplate.setReceiveTimeout(1000);
        ExecutionContextHolder.remove();
        String actual = (String) jmsTemplate.receiveAndConvert("queue");
        ExecutionContext current = ExecutionContextHolder.current();
        System.exit(expected.equals(actual) && "zone1".equals(current.get("favorite-zone")) ? 0 : 1);
    }

    public static void main(String[] args) {
        SpringApplication.run(JmsTest.class, "--spring.config.name=jms");
    }
}
