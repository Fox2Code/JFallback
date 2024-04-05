package jfallback.java.util;

import java.util.Collection;
import java.util.function.IntFunction;

public final class CollectionShims {
    // Added in java 11
    public static <E> E[] toArray(Collection<E> list, IntFunction<E[]> generator) {
        return list.toArray(generator.apply(0));
    }
}
