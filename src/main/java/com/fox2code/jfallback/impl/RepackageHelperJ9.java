package com.fox2code.jfallback.impl;

import com.fox2code.jfallback.impl.RepackageHelper.VersionSpecificLoader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ9 extends VersionSpecificLoader {
    static final RepackageHelperJ9 INSTANCE = new RepackageHelperJ9();
    private static final HashSet<String> java9Cls = new HashSet<>(Arrays.asList(
            "java/lang/invoke/StringConcatFactory", "java/util/concurrent/Flow"
    ));
    private static final HashSet<String> java9Shims = new HashSet<>(Arrays.asList(
            "java/util/List.of()Ljava/util/List;",
            "java/util/List.of(Ljava/lang/Object;)Ljava/util/List;",
            "java/util/Set.of()Ljava/util/Set;",
            "java/util/Set.of(Ljava/lang/Object;)Ljava/util/Set;",
            "java/util/Map.of()Ljava/util/Map;",
            "java/util/Map.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;",
            "java/util/Optional.ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V",
            "java/util/Optional.or(Ljava/util/function/Supplier;)Ljava/util/Optional;",
            "java/util/Optional.stream()Ljava/util/stream;",
            "java/io/InputStream.transferTo(Ljava/io/OutputStream;)J",
            // Jrt-fs workaround, act as a redirect, not as a shim
            "java/nio/file/FileSystems.newFileSystem(Ljava/net/URI;Ljava/util/Map;)Ljava/nio/file/FileSystem;"
    ));

    @Override
    Set<String> getVersionSpecificClasses() {
        return java9Cls;
    }

    @Override
    Set<String> getVersionSpecificShims() {
        return java9Shims;
    }
}
