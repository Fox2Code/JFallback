package com.fox2code.jfallback.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

final class RepackageHelperJ13 extends RepackageHelper.VersionSpecificLoader {
    static final RepackageHelperJ13 INSTANCE = new RepackageHelperJ13();
    private static final HashSet<String> java13Shims = new HashSet<>(Arrays.asList(
            "java/nio/file/FileSystems.newFileSystem(Ljava/nio/file/Path;)Ljava/nio/file/FileSystem;"
    ));

    @Override
    Set<String> getVersionSpecificShims() {
        return java13Shims;
    }
}
