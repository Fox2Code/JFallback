package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ10 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ10 INSTANCE = new RepackageHelperJ10();
    private static final HashSet<String> java10Shims = new HashSet<>(Arrays.asList(
            "java/util/List.copyOf(Ljava/util/Collection;)Ljava/util/List;",
            "java/util/Set.copyOf(Ljava/util/Collection;)Ljava/util/Set;",
            "java/util/Optional.orElseThrow()Ljava/lang/Object;",
            "java/util/stream/Collectors.toUnmodifiableList()Ljava/util/stream/Collector;"
    ));

    @Override
    Set<String> getVersionSpecificShims() {
        return java10Shims;
    }
}
