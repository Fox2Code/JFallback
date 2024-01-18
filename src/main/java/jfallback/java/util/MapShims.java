package jfallback.java.util;

import java.util.Collections;
import java.util.Map;

public final class MapShims {
    // Added in java 9
    public static <K, V> Map<K, V> of() {
        return Collections.emptyMap();
    }

    public static <K, V> Map<K, V> of(K key, V value) {
        return Collections.singletonMap(key, value);
    }
}
