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
package com.github.enadim.spring.cloud.ribbon.propagator.jms;

/**
 * Accepts property name within [(a-z)(A-Z)-_]* permuting '-' with '$'
 */
public class SimpleMessagePropertyEncoder implements MessagePropertyEncoder {

    @Override
    public String encode(String value) {
        return process(value, '-', '$');
    }

    @Override
    public String decode(String value) {
        return process(value, '$', '-');
    }

    public String process(String value, char oldChar, char newChar) {
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == oldChar) {
                chars[i] = newChar;
            } else if (!isJavaFieldChar(chars[i])) {
                throw new IllegalArgumentException("invalid property name '" + value + "'");
            }
        }
        return new String(chars);
    }

    private boolean isJavaFieldChar(char aChar) {
        return (aChar >= 'a' && aChar <= 'z') ||
                (aChar >= 'A' && aChar <= 'Z') ||
                (aChar >= '0' && aChar <= '9') ||
                aChar == '_';
    }
}
