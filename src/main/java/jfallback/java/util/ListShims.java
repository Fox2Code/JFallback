package jfallback.java.util;

import java.util.*;
import java.util.function.IntFunction;

public final class ListShims {
    // Added in java 9
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    // Added in java 9
    public static <E> List<E> of(E element) {
        return Collections.singletonList(element);
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5, E element6) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5, element6));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5, E element6, E element7) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5, element6, element7));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5, E element6, E element7, E element8) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5, element6, element7, element8));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5, E element6, E element7, E element8, E element9) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5, element6, element7, element8, element9));
    }

    // Added in java 9
    public static <E> List<E> of(E element1, E element2, E element3, E element4, E element5, E element6, E element7, E element8, E element9, E element10) {
        return Collections.unmodifiableList(Arrays.asList(
                element1, element2, element3, element4, element5, element6, element7, element8, element9, element10));
    }

    // Added in java 9
    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        if (elements.length == 0) return Collections.emptyList();
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(elements, elements.length)));
    }

    // Added in java 10
    public static <E> List<E> copyOf(Collection<? extends E> coll) {
        return Collections.unmodifiableList(new ArrayList<>(coll));
    }

    // Added in java 11
    public static <E> E[] toArray(List<E> list, IntFunction<E[]> generator) {
        return list.toArray(generator.apply(0));
    }
}
