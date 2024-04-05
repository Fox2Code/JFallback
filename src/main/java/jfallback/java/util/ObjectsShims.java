package jfallback.java.util;

import java.util.Objects;
import java.util.function.Supplier;

public final class ObjectsShims {
    // Added in java 9
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : Objects.requireNonNull(defaultObj, "defaultObj");
    }

    // Added in java 9
    public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier) {
        return (obj != null) ? obj : Objects.requireNonNull(
                Objects.requireNonNull(supplier, "supplier").get(), "supplier.get()");
    }

    // Added in java 9
    public static int checkFromToIndex(int fromIndex, int toIndex, int length) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex +
                            ") out of bounds for length " + length);
        }
        return fromIndex;
    }

    // Added in java 9
    public static int checkFromIndexSize(int fromIndex, int size, int length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + fromIndex + " + " +
                            size + ") out of bounds for length " + length);
        }
        return fromIndex;
    }
}
