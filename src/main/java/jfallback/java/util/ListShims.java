package jfallback.java.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListShims {
    // Added in java 9
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    public static <E> List<E> of(E element) {
        return Collections.singletonList(element);
    }

    // Added in java 10
    public static <E> List<E> copyOf(Collection<? extends E> coll) {
        return Collections.unmodifiableList(new ArrayList<>(coll));
    }
}
