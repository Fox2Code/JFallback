package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ11 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ11 INSTANCE = new RepackageHelperJ11();
    private static final HashSet<String> java11Pkgs = new HashSet<>(Arrays.asList(
            "java/net/http/"
    ));
    private static final HashSet<String> java11Shims = new HashSet<>(Arrays.asList(
            "java/lang/Character.toString(I)Ljava/lang/String;",
            "java/lang/String.strip()Ljava/lang/String;",
            "java/lang/String.stripLeading()Ljava/lang/String;",
            "java/lang/String.stripTrailing()Ljava/lang/String;",
            "java/lang/String.isBlank()Z",
            "java/lang/String.lines()Ljava/util/stream/Stream;",
            "java/lang/String.repeat(I)Ljava/lang/String;",
            "java/util/Optional.isEmpty()Z",
            "java/util/OptionalDouble.isEmpty()Z",
            "java/util/OptionalInt.isEmpty()Z",
            "java/util/Collection.toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;",
            "java/util/List.toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;",
            "java/util/Set.toArray(Ljava/util/function/IntFunction;)[Ljava/lang/Object;",
            "java/io/InputStream.readAllBytes()[B",
            "java/nio/file/Path.of(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;",
            "java/nio/file/Path.of(Ljava/net/URI;)Ljava/nio/Path;"
    ));

    @Override
    Set<String> getVersionSpecificPackages() {
        return java11Pkgs;
    }

    @Override
    Set<String> getVersionSpecificShims() {
        return java11Shims;
    }
}
