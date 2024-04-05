package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ16 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ16 INSTANCE = new RepackageHelperJ16();
    private static final HashSet<String> java16Cls = new HashSet<>(Arrays.asList(
            "java/lang/Record", "java/lang/reflect/RecordComponent"
    ));
    private static final HashSet<String> java16Shims = new HashSet<>(Arrays.asList(
            "java/lang/Class.isRecord()Z",
            "java/lang/Class.getRecordComponents()[Ljava/lang/reflect/RecordComponent;",
            "java/util/stream/Stream.toList()Ljava/util/List;"
    ));

    @Override
    Set<String> getVersionSpecificClasses() {
        return java16Cls;
    }

    @Override
    Set<String> getVersionSpecificShims() {
        return java16Shims;
    }
}
