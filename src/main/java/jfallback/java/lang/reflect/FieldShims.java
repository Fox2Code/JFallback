package jfallback.java.lang.reflect;

import java.lang.reflect.Field;

public final class FieldShims {
    // Added in java 9
    public static boolean trySetAccessible(Field field) {
        try {
            field.setAccessible(true);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
