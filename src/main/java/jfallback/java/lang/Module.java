package jfallback.java.lang;

import com.fox2code.jfallback.impl.RepackageHelper;
import jfallback.java.lang.module.ModuleDescriptor;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;

public final class Module {
    static final Module JAVA_BASE = new Module(null,
            ModuleDescriptor.makeJFallbackModuleDescriptor("java.base"));
    static final Module JDK_JFR;
    static final WeakHashMap<ClassLoader, Module> UNNAMED_MODULES = new WeakHashMap<>();
    private final WeakReference<ClassLoader> loader;
    private final ModuleDescriptor descriptor;

    static {
        boolean hasJfr = false;
        ClassLoader jfrClassLoader = null;
        try {
            jfrClassLoader = Class.forName(
                    "jdk.jfr.RecordingState")
                    .getClassLoader();
            hasJfr = true;
        } catch (ClassNotFoundException ignored) {}
        if (hasJfr) {
            JDK_JFR = new Module(jfrClassLoader,
                    ModuleDescriptor.makeJFallbackModuleDescriptor("jdk.jfr"));
        } else {
            JDK_JFR = null;
        }
    }

    static Module jfallbackModuleFromClass(Class<?> cls) {
        ClassLoader loader = cls.getClassLoader();
        if ((loader == null || loader == RepackageHelper.getSystemClassLoader()) &&
                (cls.getName().startsWith("jdk.management.jfr.") ||
                        cls.getName().startsWith("jdk.jfr."))) {
            return JDK_JFR;
        }
        if (loader == null || cls.getName().startsWith("java.lang.") ||
                cls.getName().startsWith("jfallback.java.lang.")) {
            return JAVA_BASE;
        }
        return UNNAMED_MODULES.computeIfAbsent(loader, Module::new);
    }

    static Module jfallbackModuleFromName(String name) {
        switch (name) {
            default:
                return null;
            case "java.base":
                return JAVA_BASE;
            case "jdk.jfr":
                return JDK_JFR;
        }
    }

    private Module(ClassLoader loader) {
        this(loader, null);
    }

    private Module(ClassLoader loader, ModuleDescriptor descriptor) {
        this.loader = loader == null ? null : new WeakReference<>(loader);
        this.descriptor = descriptor;
    }

    // Added in java 9
    public boolean isNamed() {
        return this.descriptor != null;
    }

    // Added in java 9
    public String getName() {
        return this.descriptor != null ? this.descriptor.getName() : null;
    }

    // Added in java 9
    public ClassLoader getClassLoader() {
        return this.loader == null ? null :
                Objects.requireNonNull(this.loader.get());
    }

    // Added in java 9
    public ModuleDescriptor getDescriptor() {
        return this.descriptor;
    }

    // Added in java 9
    public ModuleLayer getLayer() {
        return ModuleLayer.BOOT_MODULE_LAYER;
    }
}
