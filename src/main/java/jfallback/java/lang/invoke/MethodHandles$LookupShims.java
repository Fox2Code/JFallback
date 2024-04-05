package jfallback.java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class MethodHandles$LookupShims {
    // Added in java 9
    public static VarHandle findVarHandle(
            MethodHandles.Lookup lookup, Class<?> decl, String name, Class<?> type)
            throws NoSuchFieldException, IllegalAccessException {
        MethodHandle getter = lookup.findGetter(decl, name, type);
        MethodHandle setter = null;
        String finalErrMsg = null;
        try {
            setter = lookup.findSetter(decl, name, type);
        } catch (IllegalAccessException illegalAccessException) {
            finalErrMsg = illegalAccessException.getMessage();
        }
        return new VarHandle(getter, setter, finalErrMsg);
    }

    // Added in java 9
    public static VarHandle findStaticVarHandle(
            MethodHandles.Lookup lookup, Class<?> decl, String name, Class<?> type)
            throws NoSuchFieldException, IllegalAccessException {
        MethodHandle getter = lookup.findStaticGetter(decl, name, type);
        MethodHandle setter = null;
        String finalErrMsg = null;
        try {
            setter = lookup.findStaticSetter(decl, name, type);
        } catch (IllegalAccessException illegalAccessException) {
            finalErrMsg = illegalAccessException.getMessage();
        }
        return new VarHandle(getter, setter, finalErrMsg);
    }
}
