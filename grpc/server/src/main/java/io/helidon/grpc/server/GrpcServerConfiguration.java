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

import java.util.function.Supplier;

import io.helidon.config.Config;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

/**
 * The configuration for a gRPC server.
 *
 * @author Jonathan Knight
 */
public interface GrpcServerConfiguration {
    /**
     * The default server name.
     */
    String DEFAULT_NAME = "grpc.server";

    /**
     * The default grpc port.
     */
    int DEFAULT_PORT = 1408;

    /**
     * The default number of worker threads that will be used if not explicitly set.
     */
    int DEFAULT_WORKER_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Get the server name.
     *
     * @return the server name
     */
    String name();

    /**
     * Get the server port.
     *
     * @return the server port
     */
    int port();

    /**
     * Determine whether use native transport if possible.
     * <p>
     * If native transport support is enabled, gRPC server will use epoll on
     * Linux, or kqueue on OS X. Otherwise, the standard NIO transport will
     * be used.
     *
     * @return {@code true} if native transport should be used
     */
    boolean useNativeTransport();

    /**
     * Returns an <a href="http://opentracing.io">opentracing.io</a> tracer. Default is {@link GlobalTracer}.
     *
     * @return a tracer to use - never {@code null} (defaulting to {@link GlobalTracer}
     */
    Tracer tracer();

    /**
     * Returns tracing configuration.
     *
     * @return a tracing configuration.
     */
    TracingConfiguration tracingConfig();

    /**
     * Returns a count of threads in s pool used to tryProcess gRPC requests.
     * <p>
     * Default value is {@code CPU_COUNT * 2}.
     *
     * @return a workers count
     */
    int workers();

    /**
     * Returns a SslConfiguration to use with the server socket. If not {@code null} then
     * the server enforces an SSL communication.
     *
     * @return a SSL context to use
     */
    SslConfiguration sslConfig();

    /**
     * Creates new instance with default values for all configuration properties.
     *
     * @return a new instance
     */
    static GrpcServerConfiguration create() {
        return builder().build();
    }

    /**
     * Creates new instance with values from external configuration.
     *
     * @param config the externalized configuration
     * @return a new instance
     */
    static GrpcServerConfiguration create(Config config) {
        return builder(config).build();
    }

    /**
     * Creates new instance of a {@link Builder server configuration builder}.
     *
     * @return a new builder instance
     */
    static GrpcServerConfiguration.Builder builder() {
        return new Builder();
    }

    /**
     * Creates new instance of a {@link Builder server configuration builder} with defaults from external configuration source.
     *
     * @param config the externalized configuration
     * @return a new builder instance
     */
    static Builder builder(Config config) {
        return new Builder().config(config);
    }

    /**
     * A {@link GrpcServerConfiguration} builder.
     */
    final class Builder implements io.helidon.common.Builder<GrpcServerConfiguration> {
        private String name = DEFAULT_NAME;

        private int port = DEFAULT_PORT;

        private boolean useNativeTransport;

        private Tracer tracer;

        private TracingConfiguration tracingConfig;

        private int workers;

        private SslConfiguration sslConfig = null;

        private Builder() {
        }

        public GrpcServerConfiguration.Builder config(Config config) {
            if (config == null) {
                return this;
            }

            name = config.get("name").asString().orElse(DEFAULT_NAME);
            port = config.get("port").asInt().orElse(DEFAULT_PORT);
            useNativeTransport = config.get("native").asBoolean().orElse(false);
            config.get("workers").asInt().ifPresent(this::workersCount);

            return this;
        }

        /**
         * Set the name of the gRPC server.
         * <p>
         * Configuration key: {@code name}
         *
         * @param name  the name of the gRPC server
         *
         * @return an updated builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets server port. If port is {@code 0} or less then any available ephemeral port will be used.
         * <p>
         * Configuration key: {@code port}
         *
         * @param port the server port
         * @return an updated builder
         */
        public Builder port(int port) {
            this.port = port < 0 ? 0 : port;
            return this;
        }

        /**
         * Sets an <a href="http://opentracing.io">opentracing.io</a> tracer. (Default is {@link GlobalTracer}.)
         *
         * @param tracer a tracer to set
         * @return an updated builder
         */
        public Builder tracer(Tracer tracer) {
            this.tracer = tracer;
            return this;
        }

        /**
         * Sets an <a href="http://opentracing.io">opentracing.io</a> tracer. (Default is {@link GlobalTracer}.)
         *
         * @param tracerBuilder a tracer builder to set; will be built as a first step of this method execution
         * @return updated builder
         */
        public Builder tracer(Supplier<? extends Tracer> tracerBuilder) {
            this.tracer = tracerBuilder != null ? tracerBuilder.get() : null;
            return this;
        }

        /**
         * Set trace configuration.
         *
         * @param tracingConfig the tracing configuration to set
         * @return an updated builder
         */
        public Builder tracingConfig(TracingConfiguration tracingConfig) {
            this.tracingConfig = tracingConfig;
            return this;
        }

        /**
         * Sets a count of threads in pool used to tryProcess HTTP requests.
         * Default value is {@code CPU_COUNT * 2}.
         * <p>
         * Configuration key: {@code workers}
         *
         * @param workers a workers count
         * @return an updated builder
         */
        public Builder workersCount(int workers) {
            this.workers = workers;
            return this;
        }

        /**
         * Configures SslConfiguration to use with the server socket. If not {@code null} then
         * the server enforces an SSL communication.
         *
         * @param sslConfig a SSL context to use
         * @return this builder
         */
        public Builder sslConfig(SslConfiguration sslConfig) {
            this.sslConfig = sslConfig;
            return this;
        }

        @Override
        public GrpcServerConfiguration build() {
            return new GrpcServerBasicConfig(name,
                                             port,
                                             workers,
                                             useNativeTransport,
                                             tracer,
                                             tracingConfig,
                                             sslConfig);
        }
    }
}
