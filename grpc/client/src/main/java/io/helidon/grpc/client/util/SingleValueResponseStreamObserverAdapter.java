package io.helidon.grpc.client.util;

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

import java.util.concurrent.CompletableFuture;

import io.grpc.stub.StreamObserver;

/**
 * A simple {@link io.grpc.stub.StreamObserver} adapter class. Used internally and by test code.
 *
 * @param <T> The type of objects received in this stream.
 */
public class SingleValueResponseStreamObserverAdapter<T>
        implements StreamObserver<T> {

    private int count;

    private T result;

    private CompletableFuture<T> resultFuture = new CompletableFuture<>();

    /**
     * Create a SingleValueResponseStreamObserverAdapter.
     */
    public SingleValueResponseStreamObserverAdapter() {
    }

    /**
     * Gte the Completablefuture.
     * @return The Completablefuture.
     */
    public CompletableFuture<T> getFuture() {
        return resultFuture;
    }

    @Override
    public void onNext(T value) {
        if (count++ == 0) {
            result = value;
        } else {
            resultFuture.completeExceptionally(new IllegalStateException("More than one result received."));
        }

    }

    @Override
    public void onError(Throwable t) {
        resultFuture.completeExceptionally(t);
    }

    @Override
    public void onCompleted() {
        resultFuture.complete(result);
    }

}
