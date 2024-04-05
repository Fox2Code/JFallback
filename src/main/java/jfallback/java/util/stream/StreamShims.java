package jfallback.java.util.stream;

import java.util.*;
import java.util.stream.Stream;

public final class StreamShims {
    // Added in java 16
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Stream<T> stream) {
        return (List<T>) Collections.unmodifiableList(Arrays.asList(stream.toArray()));
    }
}
