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

import org.hamcrest.Matchers;
import org.junit.Test;

import javax.jms.ConnectionFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PreservesMessagePropertiesConnectionFactoryAdapterTest {

    ConnectionFactory delegate = mock(ConnectionFactory.class);
    PreservesMessagePropertiesConnectionFactoryAdapter propagator = new PreservesMessagePropertiesConnectionFactoryAdapter(delegate, null, null);

    @Test
    public void createConnection() throws Exception {
        assertThat(propagator.createConnection().getClass(), Matchers.equalTo(PreservesMessagePropertiesConnectionAdapter.class));
        verify(delegate).createConnection();
    }

    @Test
    public void createConnectionWithCredential() throws Exception {
        assertThat(propagator.createConnection(null, null).getClass(), Matchers.equalTo(PreservesMessagePropertiesConnectionAdapter.class));
        verify(delegate).createConnection(null, null);
    }

}