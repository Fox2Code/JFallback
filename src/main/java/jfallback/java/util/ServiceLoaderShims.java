package jfallback.java.util;

import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class ServiceLoaderShims {
    // Added in java 9
    public static <S> Stream<ServiceLoader$Provider<S>> stream(ServiceLoader<S> serviceLoader) {
        Stream.Builder<ServiceLoader$Provider<S>> stream = Stream.builder();
        for (S service : serviceLoader) {
            stream.accept(new ServiceLoader$Provider<S>() {
                @SuppressWarnings("unchecked")
                @Override
                public Class<? extends S> type() {
                    return (Class<? extends S>) service.getClass();
                }

                @Override
                public S get() {
                    return service;
                }
            });
        }
        return stream.build();
    }
}
