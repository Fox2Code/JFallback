package jfallback.java.util.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class CollectorsShims {
    // Added in java 10
    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return Collector.of((Supplier<List<T>>) ArrayList::new, List::add,
                (left, right) -> { left.addAll(right); return left; },
                Collections::unmodifiableList);
    }
}
