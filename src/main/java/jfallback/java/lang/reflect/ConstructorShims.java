package jfallback.java.lang.reflect;

import java.lang.reflect.Constructor;

public final class ConstructorShims {
    // Added in java 9
    public static boolean trySetAccessible(Constructor<?> constructor) {
        try {
            constructor.setAccessible(true);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
