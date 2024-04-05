package jfallback.java.lang.reflect;

import java.lang.reflect.Method;

public final class MethodShims {
    // Added in java 9
    public static boolean trySetAccessible(Method method) {
        try {
            method.setAccessible(true);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
