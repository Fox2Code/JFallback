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
