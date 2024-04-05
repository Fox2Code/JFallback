package jfallback.java.lang;

import java.util.ResourceBundle;
import java.util.function.Supplier;

// Added in java 9
public interface System$Logger {
    String getName();
    boolean isLoggable(System$Logger$Level level);
    default void log(System$Logger$Level level, String msg) { log(level, null, msg, (Object[]) null); }
    default void log(System$Logger$Level level, Supplier<String> msgSupplier) {
        if (isLoggable(level)) { log(level, null, msgSupplier.get(), (Object[]) null); }
    }
    default void log(System$Logger$Level level, Object obj) {
        if (isLoggable(level)) { this.log(level, null, obj.toString(), (Object[]) null); }
    }
    default void log(System$Logger$Level level, String msg, Throwable thrown) { this.log(level, null, msg, thrown); }
    default void log(System$Logger$Level level, Supplier<String> msgSupplier, Throwable thrown) {
        if (isLoggable(level)) { this.log(level, null, msgSupplier.get(), thrown); }
    }
    default void log(System$Logger$Level level, String format, Object... params) {
        this.log(level, null, format, params);
    }
    void log(System$Logger$Level level, ResourceBundle bundle, String msg, Throwable thrown);
    void log(System$Logger$Level level, ResourceBundle bundle, String format, Object... params);
}
