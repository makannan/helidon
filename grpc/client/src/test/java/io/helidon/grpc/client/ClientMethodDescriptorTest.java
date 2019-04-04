/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.helidon.grpc.client.test.EchoServiceGrpc;
import org.eclipse.microprofile.metrics.MetricType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;

/**
 * @author Jonathan Knight
 * @author Mahesh Kannan
 */
@SuppressWarnings("unchecked")
public class ClientMethodDescriptorTest {
    @Test
    public void shouldCreateMethodDescriptor() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.create("foo", grpcDescriptor);

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(nullValue()));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));

        io.grpc.MethodDescriptor methodDescriptor = descriptor.descriptor();
        assertThat(methodDescriptor.getFullMethodName(), is("EchoService/foo"));
    }

    @Test
    public void shouldBuildMethodDescriptorWithCounterMetric() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .counted()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.COUNTER));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithHistogramMetric() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .histogram()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.HISTOGRAM));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithMeterMetric() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .metered()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.METERED));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithTimerMetric() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .timed()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.TIMER));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithDisabledMetric() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .disableMetrics()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.INVALID));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithContextValue() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        Context.Key<String> key = Context.key("test");
        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .addContextKey(key, "test-value")
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(nullValue()));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(1));
        assertThat(descriptor.context().get(key), is("test-value"));
    }

    @Test
    public void shouldAddZeroInterceptors() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor<?, ?> descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .intercept()
                .build();

        assertThat(descriptor.interceptors(), is(emptyIterable()));
    }

    @Test
    public void shouldAddOneInterceptor() {
        ClientInterceptor interceptor = mock(ClientInterceptor.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor<?, ?> descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .intercept(interceptor)
                .build();

        assertThat(descriptor.interceptors(), contains(interceptor));
    }

    @Test
    public void shouldAddMultipleInterceptors() {
        ClientInterceptor interceptor1 = mock(ClientInterceptor.class);
        ClientInterceptor interceptor2 = mock(ClientInterceptor.class);
        ClientInterceptor interceptor3 = mock(ClientInterceptor.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        ClientMethodDescriptor<?, ?> descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .intercept(interceptor1, interceptor2)
                .intercept(interceptor3)
                .build();

        assertThat(descriptor.interceptors(), contains(interceptor1, interceptor2, interceptor3));
    }

    @Test
    public void shouldSetName() {
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        Context.Key<String> key = Context.key("test");
        ClientMethodDescriptor descriptor = ClientMethodDescriptor.builder("foo", grpcDescriptor)
                .fullName("Test/bar")
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.descriptor().getFullMethodName(), is("Test/bar"));
    }
}
