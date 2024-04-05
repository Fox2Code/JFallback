package jfallback.java.lang;

import java.util.Objects;
import java.util.Optional;

public final class ModuleLayer {
    static final ModuleLayer EMPTY_MODULE_LAYER = new ModuleLayer(true);
    static final ModuleLayer BOOT_MODULE_LAYER = new ModuleLayer(false);

    private final boolean empty;

    private ModuleLayer(boolean empty) {
        this.empty = empty;
    }

    // Added in java 9
    public static ModuleLayer empty() {
        return EMPTY_MODULE_LAYER;
    }

    // Added in java 9
    public static ModuleLayer boot() {
        return BOOT_MODULE_LAYER;
    }

    // Added in java 9
    public Optional<Module> findModule(String name) {
        Objects.requireNonNull(name);
        if (this.empty) return Optional.empty();
        return Optional.ofNullable(Module.jfallbackModuleFromName(name));
    }

    // Added in java 9
    public ClassLoader findLoader(String name) {
        Optional<Module> optionalModule = this.findModule(name);
        if (optionalModule.isPresent()) {
            return optionalModule.get().getClassLoader();
        }
        throw new IllegalArgumentException();
    }
}
