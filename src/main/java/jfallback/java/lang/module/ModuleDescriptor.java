package jfallback.java.lang.module;

public class ModuleDescriptor {
    private final String name;

    ModuleDescriptor(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ModuleDescriptor makeJFallbackModuleDescriptor(String name) {
        return new ModuleDescriptor(name);
    }
}
