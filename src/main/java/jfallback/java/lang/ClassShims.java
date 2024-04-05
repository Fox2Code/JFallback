package jfallback.java.lang;

import jfallback.java.lang.reflect.RecordComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.WeakHashMap;

public final class ClassShims {
    // Added in java 9
    public static String getPackageName(Class<?> cls) {
        if (cls.isPrimitive()) return "java.lang";
        final String name = cls.getName();
        final int i = name.lastIndexOf('.');
        return i == -1 ? "" : name.substring(0, i);
    }

    // Added in java 9
    public static Module getModule(Class<?> cls) {
        return Module.jfallbackModuleFromClass(cls);
    }

    // Added in java 16
    public static boolean isRecord(Class<?> cls) {
        return cls.getSuperclass() == Record.class &&
                Modifier.isFinal(cls.getModifiers());
    }

    private static final WeakHashMap<Class<?>, RecordComponent[]> recordComponentsCache = new WeakHashMap<>();
    // Added in java 16
    public static RecordComponent[] getRecordComponents(Class<?> cls) {
        if (!isRecord(cls)) return null;
        RecordComponent[] recordComponents = recordComponentsCache.get(cls);
        if (recordComponents != null) return recordComponents;
        Field[] fields = cls.getDeclaredFields();
        ArrayList<RecordComponent> component = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                try {
                    Method method = cls.getDeclaredMethod(field.getName());
                    if (Modifier.isPublic(method.getModifiers())) {
                        component.add(new RecordComponent(method));
                    }
                } catch (NoSuchMethodException ignored) {}
            }
        }
        recordComponents = component.toArray(new RecordComponent[0]);
        System.out.println(cls.getName() + " -> " + Arrays.toString(fields));
        System.out.println(cls.getName() + " -> " + Arrays.toString(recordComponents));
        recordComponentsCache.put(cls, recordComponents);
        return recordComponents;
    }
}
