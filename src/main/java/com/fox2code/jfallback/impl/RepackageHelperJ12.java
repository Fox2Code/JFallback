package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ12 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ12 INSTANCE = new RepackageHelperJ12();
    private static final HashSet<String> java12Cls = new HashSet<>(Arrays.asList(
            "java/lang/constant/Constable", "java/lang/constant/ConstantDesc",
            "java/lang/invoke/TypeDescriptor"
    ));

    @Override
    Set<String> getVersionSpecificClasses() {
        return java12Cls;
    }
}
