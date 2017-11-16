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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.enadim.spring.cloud.ribbon.support.HttpLoggingConfig.HttpLoggingProperties;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.inject.Inject;

/**
 * Enables spring http logging filter.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "http.logging.enabled", matchIfMissing = true)
@EnableConfigurationProperties(HttpLoggingProperties.class)
@Slf4j
public class HttpLoggingConfig {
    @Inject
    private HttpLoggingProperties properties;


    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(properties.client);
        loggingFilter.setIncludeQueryString(properties.query);
        loggingFilter.setIncludePayload(properties.payload);
        loggingFilter.setIncludeHeaders(properties.headers);
        return loggingFilter;
    }

    @Bean
    @ConditionalOnClass(Logger.class)
    public CommonsRequestLoggingFilter springLogging() {
        Logger logger = (Logger) LoggerFactory.getLogger(CommonsRequestLoggingFilter.class);
        logger.setLevel(Level.DEBUG);
        log.info("Http logging enabled {}.", properties);
        return requestLoggingFilter();
    }

    @ConfigurationProperties("http.logging.include")
    @Getter
    @ToString
    public static class HttpLoggingProperties {
        private boolean client = true;
        private boolean query = true;
        private boolean payload = true;
        private boolean headers = true;
    }
}
