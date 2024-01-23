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
            "java/lang/String.strip()Ljava/lang/String;",
            "java/lang/String.stripLeading()Ljava/lang/String;",
            "java/lang/String.stripTrailing()Ljava/lang/String;",
            "java/lang/String.isBlank()Z",
            "java/lang/String.lines()Ljava/util/stream/Stream;",
            "java/lang/String.repeat(I)Ljava/lang/String;",
            "java/util/Optional.isEmpty()Z",
            "java/io/InputStream.readAllBytes()[B"
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
