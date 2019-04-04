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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.helidon.grpc.core.MarshallerSupplier;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import io.grpc.BindableService;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import org.eclipse.microprofile.metrics.MetricType;

import static io.helidon.grpc.core.GrpcHelper.extractMethodName;

/**
 * Encapsulates all details about a gRPC service.
 *
 * @author Mahesh Kannan
 * @author Aleksandar Seovic
 */
public class ClientServiceDescriptor {

    private final String name;
    private final Map<String, ClientMethodDescriptor> methods;
    private final List<ClientInterceptor> interceptors;
    private final Map<Context.Key<?>, Object> context;
    private final MetricType metricType;
    private Class<?> serviceClass;
    private Descriptors.FileDescriptor proto;
    private MarshallerSupplier marshallerSupplier;

    /**
     * Creates a ClientServiceDescriptor.
     *
     * @param serviceClass       The service class.
     * @param name               The service name.
     * @param proto              The proto file descriptor.
     * @param marshallerSupplier The Marchaller supplier to be used for both Marshalling & UnMarshalling.
     * @param methods            A {@link java.util.Map} of method names to {@link io.helidon.grpc.client.ClientMethodDescriptor}.
     * @param interceptors       The list of {@link io.grpc.ClientInterceptor}s.
     * @param context            The context object.
     * @param metricType         The metric type used for this service.
     */
    public ClientServiceDescriptor(Class<?> serviceClass, String name, Descriptors.FileDescriptor proto,
                                   MarshallerSupplier marshallerSupplier, Map<String, ClientMethodDescriptor> methods,
                                   List<ClientInterceptor> interceptors, Map<Context.Key<?>, Object> context,
                                   MetricType metricType) {
        this.serviceClass = serviceClass;
        this.name = name;
        this.proto = proto;
        this.marshallerSupplier = marshallerSupplier;
        this.methods = methods;
        this.interceptors = interceptors;
        this.context = context;
        this.metricType = metricType;
    }

    /**
     * Create a {@link Builder}.
     *
     * @param name the getName of the service to use to initialise the builder.
     * @return a {@link Builder}
     */
    public static Builder builder(String name) {
        return builder(name, null, null, null);
    }

    /**
     * Create a {@link Builder}.
     *
     * @param name         The getName of the service to use to initialise the builder.
     * @param serviceClass The service class.
     * @return a {@link Builder}
     */
    public static Builder builder(String name, Class serviceClass) {
        return builder(name, serviceClass, null, null);
    }

    /**
     * Create a {@link Builder}.
     *
     * @param serviceClass The service class.
     * @param sd           The {@link io.grpc.ServiceDescriptor} to use to initialize this  builder.
     * @return a {@link Builder}
     */
    public static Builder builder(Class serviceClass, io.grpc.ServiceDescriptor sd) {
        return builder(sd.getName(), serviceClass, sd.getMethods(), sd);
    }

    /**
     * Create a {@link Builder}.
     *
     * @param serviceName       The getName of the service to use to initialise the builder.
     * @param serviceClass      The service class.
     * @param methodDescriptors The methods descriptors that describe this service.
     * @param schemaDesc        The schema getDescriptor for this service.
     * @return a {@link Builder}
     */
    public static Builder builder(String serviceName, Class serviceClass,
                                  Collection<MethodDescriptor<?, ?>> methodDescriptors, Object schemaDesc) {
        return new Builder(serviceName, serviceClass, methodDescriptors, schemaDesc);
    }

    /**
     * Create a {@link Builder}.
     *
     * @param svc The BindableService.
     * @return a {@link Builder}
     */
    public static Builder builder(BindableService svc) {
        return new Builder(svc);
    }

    /**
     * Return service getName.
     *
     * @return service getName
     */
    public String name() {
        return name;
    }

    /**
     * Return {@link io.helidon.grpc.client.ClientMethodDescriptor} for a specified method getName.
     *
     * @param name   method getName
     * @param <ReqT> The request type.
     * @param <ResT> The response type.
     * @return method getDescriptor for the specified getName
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> ClientMethodDescriptor<ReqT, ResT> method(String name) {
        return (ClientMethodDescriptor<ReqT, ResT>) methods.get(name);
    }

    /**
     * Return service methods.
     *
     * @return service methods
     */
    public Collection<ClientMethodDescriptor> methods() {
        return Collections.unmodifiableCollection(methods.values());
    }

    /**
     * Return service interceptors.
     *
     * @return service interceptors
     */
    public List<ClientInterceptor> interceptors() {
        return interceptors;
    }

    /**
     * Return context map.
     *
     * @return context map
     */
    public Map<Context.Key<?>, Object> context() {
        return context;
    }

    /**
     * Return the type of metric that should be collected for this service.
     *
     * @return metric type
     */
    public MetricType metricType() {
        return metricType;
    }



    // ---- inner interface: Config -----------------------------------------

    /**
     * Fluent configuration interface for the {@link ClientServiceDescriptor}.
     */
    public interface Config {
        /**
         * Set the getName for the service.
         *
         * @param name the service getName
         * @return this {@link Config} instance for fluent call chaining
         * @throws NullPointerException     if the getName is null
         * @throws IllegalArgumentException if the getName is a blank String
         */
        Config name(String name);

        /**
         * Register the proto for the service.
         *
         * @param proto the service proto
         * @return this {@link Config} instance for fluent call chaining
         */
        Config proto(Descriptors.FileDescriptor proto);

        /**
         * Register the {@link MarshallerSupplier} for the service.
         *
         * @param marshallerSupplier the {@link MarshallerSupplier} for the service
         * @return this {@link Config} instance for fluent call chaining
         */
        Config marshallerSupplier(MarshallerSupplier marshallerSupplier);

        /**
         * Register one or more {@link ClientInterceptor interceptors} for the service.
         *
         * @param interceptors the interceptor(s) to register
         * @return this {@link Config} instance for fluent call chaining
         */
        Config intercept(ClientInterceptor... interceptors);

        /**
         * Register one or more {@link ClientInterceptor interceptors} for a named method of the service.
         *
         * @param methodName   the getName of the method to intercept
         * @param interceptors the interceptor(s) to register
         * @return this {@link Config} instance for fluent call chaining
         * @throws IllegalArgumentException if no method exists for the specified getName
         */
        Config intercept(String methodName, ClientInterceptor... interceptors);

        /**
         * Add value to the {@link Context} for the service.
         *
         * @param key   the key for the context value
         * @param value the value to add
         * @param <V>   the type of the value
         * @return this {@link Config} instance for fluent call chaining
         */
        <V> Config addContextValue(Context.Key<V> key, V value);

        /**
         * Register unary method for the service.
         *
         * @param name   the getName of the method
         * @param <ReqT> the method request type
         * @param <ResT> the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config unary(String name);

        /**
         * Register unary method for the service.
         *
         * @param name       the getName of the method
         * @param configurer the method configurer
         * @param <ReqT>     the method request type
         * @param <ResT>     the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config unary(String name,
                                  Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer);

        /**
         * Register server streaming method for the service.
         *
         * @param name   the getName of the method
         * @param <ReqT> the method request type
         * @param <ResT> the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config serverStreaming(String name);

        /**
         * Register server streaming method for the service.
         *
         * @param name       the getName of the method
         * @param configurer the method configurer
         * @param <ReqT>     the method request type
         * @param <ResT>     the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config serverStreaming(String name,
                                            Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer);

        /**
         * Register client streaming method for the service.
         *
         * @param name   the getName of the method
         * @param <ReqT> the method request type
         * @param <ResT> the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config clientStreaming(String name);

        /**
         * Register client streaming method for the service.
         *
         * @param name       the getName of the method
         * @param configurer the method configurer
         * @param <ReqT>     the method request type
         * @param <ResT>     the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config clientStreaming(String name,
                                            Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer);

        /**
         * Register bi-directional streaming method for the service.
         *
         * @param name   the getName of the method
         * @param <ReqT> the method request type
         * @param <ResT> the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config bidirectional(String name);

        /**
         * Register bi-directional streaming method for the service.
         *
         * @param name       the getName of the method
         * @param configurer the method configurer
         * @param <ReqT>     the method request type
         * @param <ResT>     the method response type
         * @return this {@link Config} instance for fluent call chaining
         */
        <ReqT, ResT> Config bidirectional(String name,
                                          Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer);

        /**
         * Collect metrics for this service using {@link org.eclipse.microprofile.metrics.Counter}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config counted();

        /**
         * Collect metrics for this service using {@link org.eclipse.microprofile.metrics.Meter}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config metered();

        /**
         * Collect metrics for this service using {@link org.eclipse.microprofile.metrics.Histogram}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config histogram();

        /**
         * Collect metrics for this service using {@link org.eclipse.microprofile.metrics.Timer}.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config timed();

        /**
         * Explicitly disable metrics collection for this service.
         *
         * @return this {@link Config} instance for fluent call chaining
         */
        Config disableMetrics();
    }

    // ---- inner class: Aware ----------------------------------------------

    /**
     * Allows users to specify that they would like to have access to a
     * {@link ClientServiceDescriptor} within their {@link ClientInterceptor}
     * implementation.
     */
    public interface Aware {
        /**
         * Set service getDescriptor.
         *
         * @param descriptor service getDescriptor instance
         */
        void setServiceDescriptor(ClientServiceDescriptor descriptor);
    }

    // ---- inner class: BaseBuilder --------------------------------------------

    /**
     * A {@link ClientServiceDescriptor} builder.
     */
    public static final class Builder
            implements Config, io.helidon.common.Builder<ClientServiceDescriptor> {
        private Class<?> serviceClass;

        private String name;
        private Descriptors.FileDescriptor proto;
        private MarshallerSupplier marshallerSupplier = MarshallerSupplier.defaultInstance();
        private Map<String, ClientMethodDescriptor.Builder> methodBuilders = new LinkedHashMap<>();
        private List<ClientInterceptor> interceptors = new ArrayList<>();
        private Map<Context.Key<?>, Object> context = new HashMap<>();
        private MetricType metricType;

        Builder(String serviceName) {
            this.name = serviceName;
        }

        Builder(String serviceName, Class serviceClass) {
            this.name = serviceName;
            this.serviceClass = serviceClass;
        }

        /**
         * Builds the ClientService from the protoc generated classes. This is typically used
         * in scenarios where the client does not want to use the generated stubs but would
         * like to invoke methods on the service.
         *
         * @param service A {@link BindableService} from which Build is bootstrapped.
         */
        Builder(BindableService service) {
            ServerServiceDefinition def = service.bindService();

            initialize(def.getServiceDescriptor().getName(), service.getClass(),
                       def.getServiceDescriptor().getMethods(), def.getServiceDescriptor().getSchemaDescriptor());
        }

        Builder(String serviceName, Class serviceClass,
                Collection<MethodDescriptor<?, ?>> methodDescriptors, Object schemaDesc) {
            initialize(serviceName, serviceClass, methodDescriptors, schemaDesc);
        }

        @SuppressWarnings("unchecked")
        private void initialize(String serviceName, Class serviceClass,
                                Collection<MethodDescriptor<?, ?>> methodDescriptors, Object schemaDesc) {
            this.name = serviceName;
            this.serviceClass = serviceClass;
            if (schemaDesc instanceof io.grpc.protobuf.ProtoFileDescriptorSupplier) {
                this.proto(((io.grpc.protobuf.ProtoFileDescriptorSupplier) schemaDesc).getFileDescriptor());
            }

            for (io.grpc.MethodDescriptor md : methodDescriptors) {
                String methodName = extractMethodName(md.getFullMethodName());

                ClientMethodDescriptor.Builder descBuilder = ClientMethodDescriptor.builder(methodName, md);

                methodBuilders.put(methodName, descBuilder);
            }

        }

        @Override
        public Builder name(String serviceName) {
            if (serviceName == null) {
                throw new NullPointerException("Service getName cannot be null");
            }

            if (serviceName.trim().isEmpty()) {
                throw new IllegalArgumentException("Service getName cannot be blank");
            }

            this.name = serviceName.trim();
            for (Map.Entry<String, ClientMethodDescriptor.Builder> e : methodBuilders.entrySet()) {
                e.getValue().fullName(serviceName + "/" + e.getKey());
            }
            return this;
        }

        @Override
        public Builder proto(Descriptors.FileDescriptor proto) {
            this.proto = proto;
            return this;
        }

        @Override
        public Builder marshallerSupplier(MarshallerSupplier marshallerSupplier) {
            this.marshallerSupplier = marshallerSupplier;
            return this;
        }

        @Override
        public Builder unary(String name) {
            return unary(name, null);
        }

        @Override
        public <ReqT, ResT> Builder unary(String name,
                                          Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer) {
            methodBuilders.put(name, createMethodDescriptor(name,
                                                            io.grpc.MethodDescriptor.MethodType.UNARY,
                                                            configurer));
            return this;
        }

        @Override
        public Builder serverStreaming(String name) {
            return serverStreaming(name, null);
        }

        @Override
        public <ReqT, ResT> Builder serverStreaming(String name,
                                                    Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer) {
            methodBuilders.put(name, createMethodDescriptor(name,
                                                            io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
                                                            configurer));
            return this;
        }

        @Override
        public Builder clientStreaming(String name) {
            return clientStreaming(name, null);
        }

        @Override
        public <ReqT, ResT> Builder clientStreaming(String name,
                                                    Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer) {
            methodBuilders.put(name, createMethodDescriptor(name,
                                                            io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING,
                                                            configurer));
            return this;
        }

        @Override
        public Builder bidirectional(String name) {
            return bidirectional(name, null);
        }

        @Override
        public <ReqT, ResT> Builder bidirectional(String name,
                                                  Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer) {
            methodBuilders.put(name, createMethodDescriptor(name,
                                                            io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING,
                                                            configurer));
            return this;
        }

        @Override
        public Builder intercept(ClientInterceptor... interceptors) {
            Collections.addAll(this.interceptors, interceptors);
            return this;
        }

        @Override
        public Builder intercept(String methodName, ClientInterceptor... interceptors) {
            ClientMethodDescriptor.Builder method = methodBuilders.get(methodName);

            if (method == null) {
                throw new IllegalArgumentException("No method exists with getName '" + methodName + "'");
            }

            method.intercept(interceptors);

            return this;
        }

        @Override
        public <V> Builder addContextValue(Context.Key<V> key, V value) {
            context.put(key, value);
            return this;
        }

        @Override
        public Builder counted() {
            return metricType(MetricType.COUNTER);
        }

        @Override
        public Builder metered() {
            return metricType(MetricType.METERED);
        }

        @Override
        public Builder histogram() {
            return metricType(MetricType.HISTOGRAM);
        }

        @Override
        public Builder timed() {
            return metricType(MetricType.TIMER);
        }

        @Override
        public Builder disableMetrics() {
            return metricType(MetricType.INVALID);
        }

        private Builder metricType(MetricType metricType) {
            this.metricType = metricType;
            return this;
        }

        public Descriptors.FileDescriptor getProto() {
            return proto;
        }

        @Override
        public ClientServiceDescriptor build() {
            Map<String, ClientMethodDescriptor> methods = new LinkedHashMap<>();

            for (Map.Entry<String, ClientMethodDescriptor.Builder> entry : methodBuilders.entrySet()) {
                methods.put(entry.getKey(), entry.getValue().build());
            }

            return new ClientServiceDescriptor(serviceClass, name, proto, marshallerSupplier,
                                               methods, interceptors, context, metricType);
        }

        // ---- helpers -----------------------------------------------------

        @SuppressWarnings("unchecked")
        private <ReqT, ResT> ClientMethodDescriptor.Builder<ReqT, ResT> createMethodDescriptor(
                String methodName,
                io.grpc.MethodDescriptor.MethodType methodType,
                Consumer<ClientMethodDescriptor.Config<ReqT, ResT>> configurer) {
            Class<ReqT> requestType = (Class<ReqT>) getTypeFromMethodDescriptor(methodName, true);
            Class<ResT> responseType = (Class<ResT>) getTypeFromMethodDescriptor(methodName, false);

            io.grpc.MethodDescriptor<ReqT, ResT> grpcDesc = io.grpc.MethodDescriptor.<ReqT, ResT>newBuilder()
                    .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName(this.name, methodName))
                    .setType(methodType)
                    .setRequestMarshaller(marshallerSupplier.get(requestType))
                    .setResponseMarshaller(marshallerSupplier.get(responseType))
                    .setSampledToLocalTracing(true)
                    .build();

            ClientMethodDescriptor.Builder<ReqT, ResT> builder = ClientMethodDescriptor.builder(methodName, grpcDesc);
            if (configurer != null) {
                configurer.accept(builder);
            }

            return builder;
        }

        private Class<?> getTypeFromMethodDescriptor(String methodName, boolean fInput) {
            // if the proto is not present, assume that we are not using
            // protobuf for marshalling and that whichever marshaller is used
            // doesn't need type information (basically, that the serialized
            // stream is self-describing)
            if (proto == null) {
                return Object.class;
            }

            // todo: add error handling here, and fail fast with a more
            // todo: meaningful exception (and message) than a NPE
            // todo: if the service or the method cannot be found
            Descriptors.ServiceDescriptor svc = proto.findServiceByName(name);
            Descriptors.MethodDescriptor mtd = svc.findMethodByName(methodName);
            Descriptors.Descriptor type = fInput ? mtd.getInputType() : mtd.getOutputType();

            String pkg = getPackageName();
            String outerClass = getOuterClassName();

            // make sure that any nested protobuf class names are converted
            // into a proper Java binary class getName
            String className = pkg + "." + outerClass + type.getFullName().replace('.', '$');

            // the assumption here is that the protobuf generated classes can always
            // be loaded by the same class loader that loaded the service class,
            // as the service implementation is bound to depend on them
            try {
                return (serviceClass != null ? serviceClass : this.getClass()).getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private String getPackageName() {
            String pkg = proto.getOptions().getJavaPackage();
            return "".equals(pkg) ? proto.getPackage() : pkg;
        }

        private String getOuterClassName() {
            DescriptorProtos.FileOptions options = proto.getOptions();
            if (options.getJavaMultipleFiles()) {
                // there is no outer class -- each message will have its own top-level class
                return "";
            }

            String outerClass = options.getJavaOuterClassname();
            if ("".equals(outerClass)) {
                outerClass = getOuterClassFromFileName(proto.getName());
            }

            // append $ in order to timed a proper binary getName for the nested message class
            return outerClass + "$";
        }

        private String getOuterClassFromFileName(String name) {
            // strip .proto extension
            name = name.substring(0, name.lastIndexOf(".proto"));

            String[] words = name.split("_");
            StringBuilder sb = new StringBuilder(name.length());

            for (String word : words) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }

            return sb.toString();
        }

    }

}
