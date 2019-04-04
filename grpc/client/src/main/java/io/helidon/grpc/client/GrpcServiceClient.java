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

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import javax.annotation.concurrent.ThreadSafe;

import io.helidon.grpc.client.util.SingleValueResponseStreamObserverAdapter;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

/**
 * A base class for gRPC Clients.
 *
 * @author Mahesh Kannan
 */
@ThreadSafe
public class GrpcServiceClient
        extends AbstractStub<GrpcServiceClient> {

    private ClientServiceDescriptor clientServiceDescriptor;

    /**
     * Create a new instance that can be used to invoke methods on the service.
     *
     * @param channel                 the {@link Channel} to connect to the server
     * @param callOptions             the {@link CallOptions}
     * @param clientServiceDescriptor The {@link io.helidon.grpc.client.ClientServiceDescriptor} that describes the service.
     */
    public GrpcServiceClient(Channel channel, CallOptions callOptions, ClientServiceDescriptor clientServiceDescriptor) {
        super(channel, callOptions);
        this.clientServiceDescriptor = clientServiceDescriptor;
    }

    @Override
    protected GrpcServiceClient build(Channel channel, CallOptions callOptions) {
        return new GrpcServiceClient(channel, callOptions, clientServiceDescriptor);
    }

    /**
     * Invoke the specified Unary method with the specified request object.
     *
     * @param methodName The unary method name to be invoked.
     * @param request    The request parameter.
     * @param <ReqT>     The request type.
     * @param <ResT>     The response type.
     * @return The result of this invocation.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> ResT blockingUnary(String methodName, ReqT request) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.UNARY);
        return (ResT) ClientCalls.blockingUnaryCall(
                getChannel(), cmd.descriptor(), getCallOptions(), request);
    }

    /**
     * Invoke the specified Unary method with the specified request object adn the response observer.
     *
     * @param methodName The unary method name to be invoked.
     * @param request    The request parameter.
     * @param <ReqT>     The request type.
     * @param <ResT>     The response type.
     * @return A {@link java.util.concurrent.CompletableFuture} to obtain the result.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> CompletableFuture<ResT> unary(String methodName, ReqT request) {
        SingleValueResponseStreamObserverAdapter<ResT> observer = new SingleValueResponseStreamObserverAdapter<>();

        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.UNARY);
        ClientCalls.asyncUnaryCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                request,
                observer);

        return observer.getFuture();
    }

    /**
     * Invoke the specified Unary method with the specified request object adn the response observer.
     *
     * @param methodName   The unary method name to be invoked.
     * @param request      The request parameter.
     * @param respObserver A {@link StreamObserver} to receive the result.
     * @param <ReqT>       The request type.
     * @param <ResT>       The response type.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> void unary(String methodName, ReqT request, StreamObserver<ResT> respObserver) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.UNARY);
        ClientCalls.asyncUnaryCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                request,
                respObserver);
    }

    /**
     * Invoke the specified server streaming method with the specified request object.
     *
     * @param methodName The unary method name to be invoked.
     * @param request    The request parameter.
     * @param <ReqT>     The request type.
     * @param <ResT>     The response type.
     * @return An {@link java.util.Iterator} to obtain results.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> Iterator<ResT> blockingServerStreaming(String methodName, ReqT request) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.SERVER_STREAMING);
        return (Iterator<ResT>) ClientCalls.blockingServerStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                request);
    }

    /**
     * Invoke the specified server streaming method with the specified request object and response Observer.
     *
     * @param methodName   The unary method name to be invoked.
     * @param request      The request parameter.
     * @param respObserver A {@link StreamObserver} to receive the result.
     * @param <ReqT>       The request type.
     * @param <ResT>       The response type.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> void serverStreaming(String methodName, ReqT request, StreamObserver<ResT> respObserver) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.SERVER_STREAMING);
        ClientCalls.asyncServerStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                request,
                respObserver);
    }

    /**
     * Invoke the specified client streaming method with the specified response Observer.
     *
     * @param methodName The unary method name to be invoked.
     * @param items      The Collection of items (of type ReqT) to be streamed.
     * @param <ReqT>     The request type.
     * @param <ResT>     The response type.
     * @return A {@link io.grpc.stub.StreamObserver} to retrieve results.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> CompletableFuture<ResT> clientStreaming(String methodName, Iterable<ReqT> items) {
        SingleValueResponseStreamObserverAdapter<ResT> obsv = new SingleValueResponseStreamObserverAdapter<>();
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.CLIENT_STREAMING);
        StreamObserver<ReqT> reqStream = ClientCalls.asyncClientStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                obsv);

        for (ReqT item : items) {
            reqStream.onNext(item);
        }
        reqStream.onCompleted();

        return obsv.getFuture();
    }

    /**
     * Invoke the specified client streaming method with the specified response Observer.
     *
     * @param methodName   The unary method name to be invoked.
     * @param respObserver A {@link StreamObserver} to receive the result.
     * @param <ReqT>       The request type.
     * @param <ResT>       The response type.
     * @return A {@link io.grpc.stub.StreamObserver} to retrieve results.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> StreamObserver<ReqT> clientStreaming(String methodName, StreamObserver<ResT> respObserver) {
        SingleValueResponseStreamObserverAdapter<ResT> obsv = new SingleValueResponseStreamObserverAdapter<>();

        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.CLIENT_STREAMING);
        return ClientCalls.asyncClientStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                respObserver);
    }

    /**
     * Invoke the specified client streaming method with the specified response Observer.
     *
     * @param methodName   The unary method name to be invoked.
     * @param items        The Collection of items (of type ReqT) to be streamed.
     * @param respObserver A {@link StreamObserver} to receive the result.
     * @param <ReqT>       The request type.
     * @param <ResT>       The response type.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> void bidiStreaming(String methodName,
                                           Iterable<ReqT> items,
                                           StreamObserver<ResT> respObserver) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.BIDI_STREAMING);
        StreamObserver<ReqT> reqStream = ClientCalls.asyncBidiStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                respObserver);

        for (ReqT item : items) {
            reqStream.onNext(item);
        }
        reqStream.onCompleted();
    }

    /**
     * Invoke the specified Bidi streaming method with the specified response Observer.
     *
     * @param methodName   The unary method name to be invoked.
     * @param respObserver A {@link StreamObserver} to receive the result.
     * @param <ReqT>       The request type.
     * @param <ResT>       The response type.
     * @return A {@link io.grpc.stub.StreamObserver} to retrieve results.
     */
    @SuppressWarnings("unchecked")
    public <ReqT, ResT> StreamObserver<ReqT> bidiStreaming(String methodName, StreamObserver<ResT> respObserver) {
        ClientMethodDescriptor cmd = ensureMethod(methodName, MethodType.BIDI_STREAMING);
        return ClientCalls.asyncBidiStreamingCall(
                getChannel().newCall(cmd.descriptor(), getCallOptions()),
                respObserver);
    }

    private <ReqT, ResT> ClientMethodDescriptor<ReqT, ResT> ensureMethod(String methodName, MethodType methodType) {
        ClientMethodDescriptor<ReqT, ResT> cmd = clientServiceDescriptor.method(methodName);
        if (cmd == null) {
            throw new IllegalArgumentException("No method named " + methodName + " registered with this service");
        }

        if (cmd.descriptor().getType() != methodType) {
            throw new IllegalArgumentException("Method (" + methodName + ") already registered with a different method type.");
        }

        return cmd;
    }

}
