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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimpleMessagePropertyEncoderTest {
    SimpleMessagePropertyEncoder simple = new SimpleMessagePropertyEncoder();

    @Test()
    public void should_fail_encoding_unsupported_value() {
        assertThatThrownBy(() -> simple.encode("*")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value1() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('a' - 1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value2() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('A' - 1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value3() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('0' - 1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value4() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('z' + 1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value5() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('Z' + 1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void should_fail_decoding_unsupported_value6() {
        assertThatThrownBy(() -> simple.decode("" + (char) ('9' + 1))).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void encode_decode_test() {
        String expected = "azAZ09Hello_-world3";
        String encoded = simple.encode(expected);
        String actual = simple.decode(encoded);
        assertThat(encoded).isEqualTo("azAZ09Hello_$world3");
        assertThat(actual).isEqualTo(expected);
        assertThat(isValidJavaIdentifier(encoded)).isTrue();
    }

    private boolean isValidJavaIdentifier(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        char[] c = s.toCharArray();
        if (!Character.isJavaIdentifierStart(c[0])) {
            return false;
        }
        for (int i = 1; i < c.length; i++) {
            if (!Character.isJavaIdentifierPart(c[i])) {
                return false;
            }
        }
        return true;
    }
}