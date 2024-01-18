package jfallback.java.util;

import java.util.*;

public final class SetShims {
    // Added in java 9
    public static <E> Set<E> of() {
        return Collections.emptySet();
    }

    public static <E> Set<E> of(E element) {
        return Collections.singleton(element);
    }

    // Added in java 10
    public static <E> Set<E> copyOf(Collection<? extends E> coll) {
        return Collections.unmodifiableSet(new HashSet<>(coll));
    }
}
