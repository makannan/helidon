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

package io.helidon.grpc.server;

import io.helidon.grpc.server.test.EchoServiceGrpc;

import io.grpc.Context;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.eclipse.microprofile.metrics.MetricType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;

/**
 * @author Jonathan Knight
 */
@SuppressWarnings("unchecked")
public class MethodDescriptorTest {
    @Test
    public void shouldCreateMethodDescriptor() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.create("foo", grpcDescriptor, handler);

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(nullValue()));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));

        io.grpc.MethodDescriptor methodDescriptor = descriptor.descriptor();
        assertThat(methodDescriptor.getFullMethodName(), is("EchoService/foo"));
    }

    @Test
    public void shouldBuildMethodDescriptorWithCounterMetric() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .counted()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.COUNTER));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithHistogramMetric() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .histogram()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.HISTOGRAM));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithMeterMetric() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .metered()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.METERED));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithTimerMetric() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .timed()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.TIMER));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithDisabledMetric() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .disableMetrics()
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(MetricType.INVALID));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(0));
    }

    @Test
    public void shouldBuildMethodDescriptorWithContextValue() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        Context.Key<String> key = Context.key("test");
        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .addContextKey(key, "test-value")
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.metricType(), is(nullValue()));
        assertThat(descriptor.callHandler(), is(sameInstance(handler)));
        assertThat(descriptor.context(), is(notNullValue()));
        assertThat(descriptor.context().size(), is(1));
        assertThat(descriptor.context().get(key), is("test-value"));
    }

    @Test
    public void shouldAddZeroInterceptors() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor<?, ?> descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .intercept()
                .build();

        assertThat(descriptor.interceptors(), is(emptyIterable()));
    }

    @Test
    public void shouldAddOneInterceptor() {
        ServerInterceptor interceptor = mock(ServerInterceptor.class);
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor<?, ?> descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .intercept(interceptor)
                .build();

        assertThat(descriptor.interceptors(), contains(interceptor));
    }

    @Test
    public void shouldAddMultipleInterceptors() {
        ServerInterceptor interceptor1 = mock(ServerInterceptor.class);
        ServerInterceptor interceptor2 = mock(ServerInterceptor.class);
        ServerInterceptor interceptor3 = mock(ServerInterceptor.class);
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        MethodDescriptor<?, ?> descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .intercept(interceptor1, interceptor2)
                .intercept(interceptor3)
                .build();

        assertThat(descriptor.interceptors(), contains(interceptor1, interceptor2, interceptor3));
    }

    @Test
    public void shouldSetName() {
        ServerCallHandler handler = mock(ServerCallHandler.class);
        io.grpc.MethodDescriptor grpcDescriptor = EchoServiceGrpc.getServiceDescriptor()
                .getMethods()
                .stream()
                .filter(md -> md.getFullMethodName().equals("EchoService/Echo"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find echo method"));

        Context.Key<String> key = Context.key("test");
        MethodDescriptor descriptor = MethodDescriptor.builder("foo", grpcDescriptor, handler)
                .fullname("Test/bar")
                .build();

        assertThat(descriptor, is(notNullValue()));
        assertThat(descriptor.name(), is("foo"));
        assertThat(descriptor.descriptor().getFullMethodName(), is("Test/bar"));
    }
}
