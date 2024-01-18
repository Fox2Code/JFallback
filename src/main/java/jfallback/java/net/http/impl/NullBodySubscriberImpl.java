package jfallback.java.net.http.impl;

import jfallback.java.net.http.HttpResponse;
import jfallback.java.util.concurrent.Flow;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NullBodySubscriberImpl<T> implements HttpResponse.BodySubscriber<T> {
    private final CompletableFuture<T> body;

    public NullBodySubscriberImpl(T object) {
        this.body = CompletableFuture.completedFuture(object);
    }

    @Override
    public CompletionStage<T> getBody() {
        return this.body;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {}

    @Override
    public void onNext(List<ByteBuffer> item) {}

    @Override
    public void onError(Throwable throwable) {}

    @Override
    public void onComplete() {}
}
