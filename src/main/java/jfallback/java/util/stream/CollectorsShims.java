package jfallback.java.util.stream;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class CollectorsShims {
    // Added in java 10
    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return Collector.of((Supplier<List<T>>) ArrayList::new, List::add,
                (left, right) -> { left.addAll(right); return left; },
                Collections::unmodifiableList);
    }

    // Added in java 10
    public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableMap(
            Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return Collector.of(HashMap<K, U>::new, (map, t) ->
                        map.put(keyMapper.apply(t), valueMapper.apply(t)),
                (left, right) -> { left.putAll(right); return left; },
                Collections::unmodifiableMap);
    }
}
