package com.fox2code.jfallback.impl;

import com.fox2code.jfallback.impl.RepackageHelper.VersionSpecificLoader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ9 extends VersionSpecificLoader {
    static final RepackageHelperJ9 INSTANCE = new RepackageHelperJ9();
    private static final HashSet<String> java9Cls = new HashSet<>(Arrays.asList(
            "java/lang/Module", "java/lang/ModuleLayer", "java/lang/module/ModuleDescriptor",
            "java/lang/invoke/StringConcatFactory", "java/lang/invoke/VarHandle",
            "java/lang/IllegalCallerException", "java/lang/StackWalker",
            "java/lang/StackWalker$Option", "java/lang/StackWalker$StackFrame",
            "java/lang/Runtime$Version", "java/util/concurrent/Flow",
            "java/lang/System$Logger", "java/lang/System$Logger$Level",
            "java/util/ServiceLoader$Provider"
    ));
    private static final HashSet<String> java9Shims = new HashSet<>(Arrays.asList(
            "java/lang/Class.getPackageName()Ljava/lang/String;",
            "java/lang/Class.getModule()Ljava/lang/Module;",
            "java/lang/StackTraceElement.getModuleName()Ljava/lang/String;",
            "java/lang/StackTraceElement.getModuleVersion()Ljava/lang/String;",
            "java/lang/StackTraceElement.getClassLoaderName()Ljava/lang/String;",
            "java/lang/System.getLogger(Ljava/lang/String;)Ljava/lang/System$Logger;",
            "java/lang/System.getLogger(Ljava/lang/String;Ljava/util/ResourceBundle;)Ljava/lang/System$Logger;",
            "java/lang/invoke/MethodHandles.privateLookupIn" +
                    "(Ljava/lang/Class;Ljava/lang/invoke/MethodHandles$Lookup;)Ljava/lang/invoke/MethodHandles$Lookup;",
            "java/lang/invoke/MethodHandles.arrayElementVarHandle(Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
            "java/lang/invoke/MethodHandles$Lookup.findVarHandle" +
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
            "java/lang/invoke/MethodHandles$Lookup.findStaticVarHandle" +
                    "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
            "java/lang/reflect/Constructor.trySetAccessible()Z",
            "java/lang/reflect/Field.trySetAccessible()Z",
            "java/lang/reflect/Method.trySetAccessible()Z",
            "java/io/InputStream.transferTo(Ljava/io/OutputStream;)J",
            "java/util/List.of()Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
            "java/util/List.of([Ljava/lang/Object;)Ljava/util/List;",
            "java/util/Set.of()Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Set.of([Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Map.of()Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Map.ofEntries([Ljava/util/Map$Entry;)Ljava/util/Map;",
            "java/util/Map.entry(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map$Entry;",
            "java/util/Objects.requireNonNullElse(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            "java/util/Objects.requireNonNullElseGet(Ljava/lang/Object;Ljava/util/function/Supplier;)Ljava/lang/Object;",
            "java/util/Objects.checkFromToIndex(III)I",
            "java/util/Objects.checkFromIndexSize(III)I",
            "java/util/Optional.ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V",
            "java/util/Optional.or(Ljava/util/function/Supplier;)Ljava/util/Optional;",
            "java/util/Optional.stream()Ljava/util/stream/Stream;",
            "java/util/OptionalDouble.ifPresentOrElse(Ljava/util/function/DoubleConsumer;Ljava/lang/Runnable;)V",
            "java/util/OptionalDouble.or(Ljava/util/function/Supplier;)Ljava/util/OptionalDouble;",
            "java/util/OptionalDouble.stream()Ljava/util/stream/DoubleStream;",
            "java/util/OptionalInt.ifPresentOrElse(Ljava/util/function/IntConsumer;Ljava/lang/Runnable;)V",
            "java/util/OptionalInt.or(Ljava/util/function/Supplier;)Ljava/util/OptionalInt;",
            "java/util/OptionalInt.stream()Ljava/util/stream/IntStream;",
            "java/util/ServiceLoader.stream()Ljava/util/stream/Stream;",
            "java/util/jar/JarFile.baseVersion()Ljava/lang/Runtime$Version;",
            "java/util/jar/JarFile.runtimeVersion()Ljava/lang/Runtime$Version;",
            // JFallback handle "<init>" shims specially by only removing the last args and calling that method.
            "java/util/jar/JarFile.<init>(Ljava/io/File;ZILjava/lang/Runtime$Version;)V",
            // Jrt-fs workaround, act as a redirect, not as a shim
            "java/nio/file/FileSystems.newFileSystem(Ljava/net/URI;Ljava/util/Map;)Ljava/nio/file/FileSystem;"
    ));
    private static final HashSet<String> java9InitShims = new HashSet<>(Arrays.asList(
            "java/lang/StackTraceElement"
    ));

    @Override
    Set<String> getVersionSpecificClasses() {
        return java9Cls;
    }

    @Override
    Set<String> getVersionSpecificShims() {
        return java9Shims;
    }

    @Override
    Set<String> getVersionSpecificInitShims() {
        return java9InitShims;
    }
}
