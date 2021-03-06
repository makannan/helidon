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

package io.helidon.grpc.examples.common;

import java.net.URI;

import io.helidon.grpc.client.ClientRequestAttribute;
import io.helidon.grpc.client.ClientTracingInterceptor;
import io.helidon.grpc.examples.common.Greet.GreetRequest;
import io.helidon.grpc.examples.common.Greet.SetGreetingRequest;
import io.helidon.tracing.TracerBuilder;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.Tracer;

/**
 * A client for the {@link GreetService}.
 *
 * @author Aleksandar Seovic
 */
public class GreetClient {

    private GreetClient() {
    }

    /**
     *  The program entry point.
     * @param args  the program arguments
     *
     * @throws Exception  if an error occurs
     */
    public static void main(String[] args) throws Exception {
        Tracer tracer = (Tracer) TracerBuilder.create("Client")
                .collectorUri(URI.create("http://localhost:9411/api/v2/spans"))
                .build();

        ClientTracingInterceptor tracingInterceptor = ClientTracingInterceptor.builder(tracer)
                .withVerbosity().withTracedAttributes(ClientRequestAttribute.ALL_CALL_OPTIONS).build();

        Channel channel = ClientInterceptors
                .intercept(ManagedChannelBuilder.forAddress("localhost", 1408).usePlaintext().build(), tracingInterceptor);

        GreetServiceGrpc.GreetServiceBlockingStub greetSvc = GreetServiceGrpc.newBlockingStub(channel);
        System.out.println(greetSvc.greet(GreetRequest.newBuilder().setName("Aleks").build()));
        System.out.println(greetSvc.setGreeting(SetGreetingRequest.newBuilder().setGreeting("Ciao").build()));
        System.out.println(greetSvc.greet(GreetRequest.newBuilder().setName("Aleks").build()));

        Thread.sleep(5000);
    }
}
