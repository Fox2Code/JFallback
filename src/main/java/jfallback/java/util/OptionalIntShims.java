package jfallback.java.util;

import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class OptionalIntShims {
    // Added in java 9
    public static void ifPresentOrElse(OptionalInt optionalInt, IntConsumer action, Runnable emptyAction) {
        if (optionalInt.isPresent()) {
            action.accept(optionalInt.getAsInt());
        } else {
            emptyAction.run();
        }
    }

    // Added in java 9
    public static  OptionalInt or(OptionalInt optionalInt, Supplier<? extends OptionalInt> supplier) {
        if (optionalInt.isPresent()) {
            return optionalInt;
        } else return supplier.get();
    }

    // Added in java 9
    public static IntStream stream(OptionalInt optionalInt) {
        if (optionalInt.isPresent()) {
            return IntStream.of(optionalInt.getAsInt());
        } else {
            return IntStream.empty();
        }
    }

    // Added in java 10
    public static int orElseThrow(OptionalInt optionalInt) {
        if (optionalInt.isPresent()) return optionalInt.getAsInt();
        throw new NullPointerException("No value present");
    }

    // Added in java 11
    public static boolean isEmpty(OptionalInt optionalInt) {
        return !optionalInt.isPresent();
    }
}
