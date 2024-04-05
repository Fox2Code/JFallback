package jfallback.java.util;

import java.util.OptionalDouble;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class OptionalDoubleShims {
    // Added in java 9
    public static void ifPresentOrElse(OptionalDouble optionalDouble, DoubleConsumer action, Runnable emptyAction) {
        if (optionalDouble.isPresent()) {
            action.accept(optionalDouble.getAsDouble());
        } else {
            emptyAction.run();
        }
    }

    // Added in java 9
    public static OptionalDouble or(OptionalDouble optionalDouble, Supplier<? extends OptionalDouble> supplier) {
        if (optionalDouble.isPresent()) {
            return optionalDouble;
        } else return supplier.get();
    }

    // Added in java 9
    public static DoubleStream stream(OptionalDouble optionalDouble) {
        if (optionalDouble.isPresent()) {
            return DoubleStream.of(optionalDouble.getAsDouble());
        } else {
            return DoubleStream.empty();
        }
    }

    // Added in java 10
    public static double orElseThrow(OptionalDouble optionalDouble) {
        if (optionalDouble.isPresent()) return optionalDouble.getAsDouble();
        throw new NullPointerException("No value present");
    }

    // Added in java 11
    public static boolean isEmpty(OptionalDouble optionalDouble) {
        return !optionalDouble.isPresent();
    }
}
