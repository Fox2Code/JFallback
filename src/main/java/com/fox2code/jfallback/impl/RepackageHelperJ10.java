package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ10 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ10 INSTANCE = new RepackageHelperJ10();
    private static final HashSet<String> java10Shims = new HashSet<>(Arrays.asList(
            "java/lang/Runtime$Version.feature()I",
            "java/lang/Runtime$Version.interim()I",
            "java/lang/Runtime$Version.update()I",
            "java/lang/Runtime$Version.patch()I",
            "java/io/Reader.transferTo(Ljava/io/Writer;)J",
            "java/util/List.copyOf(Ljava/util/Collection;)Ljava/util/List;",
            "java/util/Set.copyOf(Ljava/util/Collection;)Ljava/util/Set;",
            "java/util/Map.copyOf(Ljava/util/Map;)Ljava/util/Map;",
            "java/util/Optional.orElseThrow()Ljava/lang/Object;",
            "java/util/OptionalDouble.orElseThrow()D",
            "java/util/OptionalInt.orElseThrow()I",
            "java/util/stream/Collectors.toUnmodifiableList()Ljava/util/stream/Collector;",
            "java/util/stream/Collectors.toUnmodifiableMap" +
                    "(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/util/stream/Collector;"
    ));

    @Override
    Set<String> getVersionSpecificShims() {
        return java10Shims;
    }
}
