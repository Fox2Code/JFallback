package jfallback.java.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MapShims {
    // Added in java 9
    public static <K, V> Map<K, V> of() {
        return Collections.emptyMap();
    }

    // Added in java 9
    public static <K, V> Map<K, V> of(K key, V value) {
        return Collections.singletonMap(key, value);
    }

    // Added in java 9
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1); hashMap.put(key2, value2);
        return Collections.unmodifiableMap(hashMap);
    }

    // Added in java 9
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1); hashMap.put(key2, value2); hashMap.put(key3, value3);
        return Collections.unmodifiableMap(hashMap);
    }

    // Added in java 9
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1); hashMap.put(key2, value2); hashMap.put(key3, value3); hashMap.put(key4, value4);
        return Collections.unmodifiableMap(hashMap);
    }

    // Added in java 9
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1); hashMap.put(key2, value2); hashMap.put(key3, value3); hashMap.put(key4, value4); hashMap.put(key5, value5);
        return Collections.unmodifiableMap(hashMap);
    }

    // Added in java 9
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> Map<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
        if (entries.length == 0) return Collections.emptyMap();
        HashMap<K, V> hashMap = new HashMap<>();
        for (Map.Entry<? extends K,? extends V> entry : entries) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(hashMap);
    }

    // Added in java 9
    public static <K, V> Map.Entry<K, V> entry(final K key,final V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        return new Map.Entry<K, V>() {
            @Override public K getKey() { return key; }
            @Override public V getValue() { return value; }
            @Override public V setValue(V value) { throw new UnsupportedOperationException(); }
            @Override public boolean equals(Object o) {
                return this == o || (o instanceof Map.Entry<?, ?>
                        && key.equals(((Map.Entry<?, ?>) o).getKey())
                        && value.equals(((Map.Entry<?, ?>) o).getValue()));
            }
            @Override public int hashCode() { return Objects.hashCode(key) ^ Objects.hashCode(value); }
            @Override public String toString() { return key + "=" + value; }
        };
    }

    // Added in java 10
    public static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(new HashMap<>(map));
    }
}
