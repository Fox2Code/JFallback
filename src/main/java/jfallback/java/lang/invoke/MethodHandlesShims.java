package jfallback.java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;

public final class MethodHandlesShims {
    private static final MethodHandles.Lookup TRUSTED;

    static {
        MethodHandles.Lookup trusted = null;
        try {
            // We can only do that on this shim because
            // it will only be loaded on java 8
            Field field = MethodHandles.Lookup.class
                    .getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            trusted = (MethodHandles.Lookup) field.get(null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        TRUSTED = trusted;
    }

    // Added in java 9
    public static MethodHandles.Lookup privateLookupIn(
            Class<?> targetClass, MethodHandles.Lookup caller) throws IllegalAccessException {
        Objects.requireNonNull(caller);
        Objects.requireNonNull(targetClass);
        return TRUSTED != null ? TRUSTED : caller;
    }

    // Added in java 9
    public static VarHandle arrayElementVarHandle(Class<?> cls) {
        if (!cls.isArray()) throw new IllegalArgumentException();
        Class<?> componentType = cls.getComponentType();
        String suffix = "";
        Class<?> arg = Object.class;
        if (componentType.isPrimitive()) {
            arg = componentType;
            String name = componentType.getName();
            suffix = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        }
        MethodHandle getter;
        MethodHandle setter;
        try {
            getter = MethodHandles.publicLookup().unreflect(
                    Array.class.getDeclaredMethod("get" + suffix, Object.class, int.class));
            setter = MethodHandles.publicLookup().unreflect(
                    Array.class.getDeclaredMethod("set" + suffix, Object.class, int.class, arg));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Should never happen", e);
        }
        return new VarHandle(getter, setter, null);
    }
}
