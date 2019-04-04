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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.MethodDescriptor;
import org.eclipse.microprofile.metrics.MetricType;

import static io.helidon.grpc.core.GrpcHelper.extractNamePrefix;

/**
 * Encapsulates all metadata necessary to define a gRPC method.
 *
 * @param <ReqT> request type
 * @param <ResT> response type
 * @author Mahesh Kannan
 */
public final class ClientMethodDescriptor<ReqT, ResT> {

    /**
     * The getName of the method.
     */
    private final String name;

    /**
     * The {@link io.grpc.MethodDescriptor} for this method. This is usually obtained from protocol buffer
     * method getDescriptor (from service getDescriptor).
     */
    private io.grpc.MethodDescriptor<ReqT, ResT> descriptor;

    /**
     * The metric type to be used for collecting method level metrics.
     */
    private MetricType metricType;

    /**
     * The context to be used for method invocation.
     */
    private Map<Context.Key, Object> context = new HashMap<>();

    /**
     * The list of client interceptors for this method.
     */
    private ArrayList<ClientInterceptor> interceptors = new ArrayList<>();

    private ClientMethodDescriptor(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    private ClientMethodDescriptor(ClientMethodDescriptor other) {
        // The members in this class are not final so that the Builder can construct it much
        // more easily than having to worry about the correct order of a large number of parameters
        // in this constructor.
        this.name = other.name;
        this.metricType = other.metricType;
        this.descriptor = other.descriptor;
        this.interceptors.addAll(other.interceptors);
        this.context.putAll(other.context);
    }

    /**
     * Creates a new {@link ClientMethodDescriptor.Builder} with the specified name and {@link io.grpc.MethodDescriptor}.
     *
     * @param name   The method name.
     * @param <ReqT> The request type.
     * @param <ResT> The response type.
     * @return A new instance of a {@link io.helidon.grpc.client.ClientMethodDescriptor.Builder}
     */
    static <ReqT, ResT> Builder<ReqT, ResT> builder(String name, io.grpc.MethodDescriptor<ReqT, ResT> descriptor) {
        return new Builder<>(name, descriptor);
    }

    /**
     * Creates a new {@link ClientMethodDescriptor} with the specified name and {@link io.grpc.MethodDescriptor}.
     *
     * @param name       The method name.
     * @param descriptor The {@link io.grpc.MethodDescriptor}.
     * @param <ReqT>     The request type.
     * @param <ResT>     The response type.
     * @return A new instance of a {@link io.helidon.grpc.client.ClientMethodDescriptor.Builder}
     */
    static <ReqT, ResT> ClientMethodDescriptor<ReqT, ResT> create(String name, io.grpc.MethodDescriptor<ReqT, ResT> descriptor) {
        return builder(name, descriptor).build();
    }

    /**
     * Returns the simple name of the method.
     *
     * @return The simple name of the method.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the {@link io.grpc.MethodDescriptor} of this method.
     *
     * @return The {@link io.grpc.MethodDescriptor} of this method.
     */
    public MethodDescriptor<ReqT, ResT> descriptor() {
        return descriptor;
    }

    /**
     * Returns the {@link org.eclipse.microprofile.metrics.MetricType} of this method.
     *
     * @return The {@link org.eclipse.microprofile.metrics.MetricType} of this method.
     */
    public MetricType metricType() {
        return metricType;
    }

    /**
     * Returns the context.
     *
     * @return The context object.
     */
    public Map<Context.Key, Object> context() {
        return context;
    }

    /**
     * Obtain the {@link ClientInterceptor}s to use for this method.
     *
     * @return the {@link ClientInterceptor}s to use for this method
     */
    List<ClientInterceptor> interceptors() {
        return Collections.unmodifiableList(interceptors);
    }

    /**
     * ClientMethod configuration API.
     *
     * @param <ReqT> request type
     * @param <ResT> response type
     */
    public interface Config<ReqT, ResT> {

        /**
         * Collect metrics for this method using {@link org.eclipse.microprofile.metrics.Counter}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> counted();

        /**
         * Collect metrics for this method using {@link org.eclipse.microprofile.metrics.Meter}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> metered();

        /**
         * Collect metrics for this method using {@link org.eclipse.microprofile.metrics.Histogram}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> histogram();

        /**
         * Collect metrics for this method using {@link org.eclipse.microprofile.metrics.Timer}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> timed();

        /**
         * Explicitly disable metrics collection for this service.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> disableMetrics();

        /**
         * Add a {@link Context.Key} and value to be added to the call {@link Context}
         * when this method is invoked.
         *
         * @param key   the {@link Context.Key} to add
         * @param value the value to map to the {@link Context.Key}
         * @param <T>   the type of the {@link Context.Key} and value
         * @return this {@link Config} instance for fluent call chaining
         * @throws NullPointerException if the key parameter is null
         */
        <T> Config<ReqT, ResT> addContextKey(Context.Key<T> key, T value);

        /**
         * Register one or more {@link ClientInterceptor interceptors} for the method.
         *
         * @param interceptors the interceptor(s) to register
         * @return this {@link io.helidon.grpc.client.ClientMethodDescriptor.Config} instance for fluent call chaining
         */
        Config<ReqT, ResT> intercept(ClientInterceptor... interceptors);
    }

    /**
     * {@link MethodDescriptor} builder implementation.
     *
     * @param <ReqT> request type
     * @param <ResT> response type
     */
    public static class Builder<ReqT, ResT>
            implements Config<ReqT, ResT> {

        // This is the currently built ClientMethodDescriptor.
        private ClientMethodDescriptor<ReqT, ResT> cmd;

        private io.grpc.MethodDescriptor.Builder<ReqT, ResT> descBuilder;

        /**
         * Constructs a new Builder instance.
         *
         * @param name The method name.
         * @param descriptor The gRPC method descriptor.
         */
        Builder(String name, MethodDescriptor<ReqT, ResT> descriptor) {
            cmd = new ClientMethodDescriptor<>(name);

            String fullName = descriptor.getFullMethodName();
            String prefix = extractNamePrefix(fullName);

            this.descBuilder = descriptor.toBuilder()
                    .setFullMethodName(prefix + "/" + name);
        }

        @Override
        public Builder<ReqT, ResT> counted() {
            return metricType(MetricType.COUNTER);
        }

        @Override
        public Builder<ReqT, ResT> metered() {
            return metricType(MetricType.METERED);
        }

        @Override
        public Builder<ReqT, ResT> histogram() {
            return metricType(MetricType.HISTOGRAM);
        }

        @Override
        public Builder<ReqT, ResT> timed() {
            return metricType(MetricType.TIMER);
        }

        @Override
        public Builder<ReqT, ResT> disableMetrics() {
            return metricType(MetricType.INVALID);
        }

        /**
         * Sets the full name of this Method.
         *
         * @param name the full name of the method.
         * @return This builder instance for fluent API.
         */
        public Builder<ReqT, ResT> fullName(String name) {
            descBuilder.setFullMethodName(name);
            return this;
        }

        private Builder<ReqT, ResT> metricType(MetricType metricType) {
            cmd.metricType = metricType;
            return this;
        }

        @Override
        public <T> Builder<ReqT, ResT> addContextKey(Context.Key<T> key, T value) {
            cmd.context.put(Objects.requireNonNull(key, "The context key cannot be null"), value);
            return this;
        }

        @Override
        public Builder<ReqT, ResT> intercept(ClientInterceptor... interceptors) {
            Collections.addAll(cmd.interceptors, interceptors);
            return this;
        }

        /**
         * Builds and returns a new instance of {@link ClientMethodDescriptor}.
         *
         * @return A new instance of {@link ClientMethodDescriptor}.
         */
        public ClientMethodDescriptor<ReqT, ResT> build() {
            cmd.descriptor = descBuilder.build();
            return new ClientMethodDescriptor<>(cmd);
        }

    }
}
