package jfallback.java.util;

import java.util.function.Supplier;

public interface ServiceLoader$Provider<S> extends Supplier<S> {
    Class<? extends S> type();

    @Override S get();
}
