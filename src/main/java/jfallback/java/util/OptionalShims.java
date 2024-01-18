package jfallback.java.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalIsPresent"})
public final class OptionalShims {
    // Added in java 9
    public static <T> void ifPresentOrElse(Optional<T> optional, Consumer<? super T> action, Runnable emptyAction) {
        if (optional.isPresent()) {
            action.accept(optional.get());
        } else {
            emptyAction.run();
        }
    }

    @SuppressWarnings("unchecked")
    public static  <T> Optional<T> or(Optional<T> optional, Supplier<? extends Optional<? extends T>> supplier) {
        if (optional.isPresent()) {
            return optional;
        } else return (Optional<T>) supplier.get();
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        if (optional.isPresent()) {
            return Stream.of(optional.get());
        } else {
            return Stream.empty();
        }
    }

    // Added in java 10
    public static <T> T orElseThrow(Optional<T> optional) {
        if (optional.isPresent()) return optional.get();
        throw new NullPointerException("No value present");
    }

    // Added in java 11
    public static boolean isEmpty(Optional<?> optional) {
        return !optional.isPresent();
    }
}
