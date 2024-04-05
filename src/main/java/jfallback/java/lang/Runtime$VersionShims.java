package jfallback.java.lang;

import java.util.List;

public final class Runtime$VersionShims {
    private static int getVersionSpot(Runtime$Version runtimeVersion, int i) {
        List<Integer> version = runtimeVersion.version();
        return (version.size() > i ? version.get(i) : 0);
    }

    // Added in java 10
    public static int feature(Runtime$Version runtimeVersion) {
        return getVersionSpot(runtimeVersion, 0);
    }

    // Added in java 10
    public static int interim(Runtime$Version runtimeVersion) {
        return getVersionSpot(runtimeVersion, 1);
    }

    // Added in java 10
    public static int update(Runtime$Version runtimeVersion) {
        return getVersionSpot(runtimeVersion, 2);
    }

    // Added in java 10
    public static int patch(Runtime$Version runtimeVersion) {
        return getVersionSpot(runtimeVersion, 3);
    }
}
