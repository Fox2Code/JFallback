package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ15 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ15 INSTANCE = new RepackageHelperJ15();
    private static final HashSet<String> java15Shims = new HashSet<>(Arrays.asList(
            "java/lang/String.formatted([Ljava/lang/Object;)Ljava/lang/String;"
    ));

    @Override
    Set<String> getVersionSpecificShims() {
        return java15Shims;
    }
}
