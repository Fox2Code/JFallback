package jfallback.java.util.jar;

import jfallback.java.lang.Runtime$Version;

public final class JarFileShims {
    private static final Runtime$Version JAVA_8 = Runtime$Version.parse("8");

    // Added in java 9
    public static Runtime$Version baseVersion() {
        return JAVA_8;
    }

    // Added in java 9
    public static Runtime$Version runtimeVersion() {
        return JAVA_8;
    }
}
