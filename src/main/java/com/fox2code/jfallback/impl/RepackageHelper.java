package com.fox2code.jfallback.impl;

import java.util.*;

public final class RepackageHelper {
    private static final HashSet<String> empty = new HashSet<>();
    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    private static final int jvmVersion = getJvmVersion0();
    private static final int maxJvmVersion = 17;
    private static final int recommendedJvmVersion = 11;
    private static final int targetJvmVersion = Math.min(Integer.getInteger(
            "jfallback.targetJvmVersion", recommendedJvmVersion), maxJvmVersion);
    public static final RepackageHelper DEFAULT =
            new RepackageHelper(jvmVersion, targetJvmVersion, false);
    public static final RepackageHelper RECURSIVE =
            new RepackageHelper(jvmVersion, targetJvmVersion, true);
    public static final String ASM_URL_CLASS_LOADER = "java/net/URLClassLoader";
    public static final String ASM_JFALLBACK_CLASS_LOADER = "com/fox2code/jfallback/JFallbackClassLoader";
    public static final String ASM_CLASS_LOADER = "java/lang/ClassLoader";
    public static final String ASM_SECURE_CLASS_LOADER = "java/security/SecureClassLoader";
    public static final String ASM_JFALLBACK_SECURE_CLASS_LOADER =
            "com/fox2code/jfallback/JFallbackClassLoader$JFallbackSecureClassLoader";

    private static int getJvmVersion0() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }

    public static int getJvmVersion() {
        return jvmVersion;
    }

    public static ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }

    public static void forceInit() {}

    final int jvmTarget;
    final int maxBytecodeTarget;
    final boolean applyRecursively;
    final HashSet<String> fallbackClasses;
    final HashSet<String> fallbackPackages;
    final HashSet<String> fallbackShims;
    final HashSet<String> fallbackInitShims;
    final List<String> classLoaderSubLoadingPaths;
    Object asmRemapper;

    public RepackageHelper(int jvmTarget, int maxBytecodeTarget) {
        this(jvmTarget, maxBytecodeTarget, false);
    }

    private RepackageHelper(int jvmTarget, int maxBytecodeTarget, boolean applyRecursively) {
        this.jvmTarget = jvmTarget;
        this.maxBytecodeTarget = maxBytecodeTarget;
        this.applyRecursively = applyRecursively;
        if (jvmTarget >= maxBytecodeTarget) {
            this.fallbackClasses = empty;
            this.fallbackPackages = empty;
            this.fallbackShims = empty;
            this.fallbackInitShims = empty;
            this.classLoaderSubLoadingPaths = Collections.emptyList();
        } else if (DEFAULT != null && jvmTarget == DEFAULT.jvmTarget &&
                maxBytecodeTarget == DEFAULT.maxBytecodeTarget) {
            this.fallbackClasses = DEFAULT.fallbackClasses;
            this.fallbackPackages = DEFAULT.fallbackPackages;
            this.fallbackShims = DEFAULT.fallbackShims;
            this.fallbackInitShims = DEFAULT.fallbackInitShims;
            this.asmRemapper = DEFAULT.asmRemapper;
            this.classLoaderSubLoadingPaths = DEFAULT.classLoaderSubLoadingPaths;
        } else {
            this.fallbackClasses = new HashSet<>();
            this.fallbackPackages = new HashSet<>();
            this.fallbackShims = new HashSet<>();
            this.fallbackInitShims = new HashSet<>();
            this.classLoaderSubLoadingPaths = new ArrayList<>();

            for (int jvm = jvmTarget + 1; jvm <= maxBytecodeTarget; jvm++) {
                VersionSpecificLoader versionSpecificLoader =
                        VersionSpecificLoader.forJvm(jvm);
                if (versionSpecificLoader == null) continue;
                this.fallbackClasses.addAll(
                        versionSpecificLoader.getVersionSpecificClasses());
                this.fallbackPackages.addAll(
                        versionSpecificLoader.getVersionSpecificPackages());
                this.fallbackShims.addAll(
                        versionSpecificLoader.getVersionSpecificShims());
                this.fallbackInitShims.addAll(
                        versionSpecificLoader.getVersionSpecificInitShims());
            }
            // Don't load java 9 prefix on java 8 as it causes many compatibility issues
            for (int jvm = this.jvmTarget < 9 ? 10 : 9; jvm <= maxBytecodeTarget; jvm++) {
                this.classLoaderSubLoadingPaths.add(0, "META-INF/versions/" + jvm + "/");
            }
            if (this.jvmTarget >= 9) {
                // On java9 and later, ask mapper to unmap class used in newer shims
                this.fallbackClasses.add("jfallback/java/util/concurrent/Flow");
                this.fallbackClasses.add("jfallback/java/lang/Runtime$Version");
            }
            // Assume full class implementations are always up-to-date
            this.fallbackShims.removeIf(k ->
                    this.fallbackClasses.contains(
                            k.substring(0, k.indexOf('.'))));
            this.fallbackInitShims.removeIf(
                    this.fallbackClasses::contains);
        }
    }

    public List<String> getClassLoaderSubLoadingPaths() {
        return this.classLoaderSubLoadingPaths;
    }

    public String clsRepackage(String cls) {
        return asmRepackage(cls.replace('.', '/')).replace('/', '.');
    }

    public String asmRepackage(String asm) {
        // If recursively applying, transform.
        if (this.applyRecursively && ASM_URL_CLASS_LOADER.equals(asm)) {
            return ASM_JFALLBACK_CLASS_LOADER;
        }
        if (this.fallbackClasses.contains(asm)) {
            if (asm.startsWith("jfallback/")) {
                return asm.substring(10);
            }
            return "jfallback/" + asm;
        }
        return asm;
    }
    public String asmRenameMethod(String owner, String name, String descriptor) {
        if (!this.applyRecursively || !name.equals("defineClass")) return name;
        switch (descriptor) {
            default: return name;
            // Supported descriptors
            case "(Ljava/lang/String;[BII)Ljava/lang/Class;":
            case "(Ljava/lang/String;[BIILjava/security/CodeSource;)Ljava/lang/Class;":
            case "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;":
                return "defineClassJFallback";
        }
    }

    abstract static class VersionSpecificLoader {
        Set<String> getVersionSpecificClasses() { return Collections.emptySet(); }

        Set<String> getVersionSpecificPackages() { return Collections.emptySet(); }

        Set<String> getVersionSpecificShims() { return Collections.emptySet(); }

        Set<String> getVersionSpecificInitShims() { return Collections.emptySet(); }

        public static VersionSpecificLoader forJvm(int jvm) {
            switch (jvm) {
                default:
                    return null;
                case 9:
                    return RepackageHelperJ9.INSTANCE;
                case 10:
                    return RepackageHelperJ10.INSTANCE;
                case 11:
                    return RepackageHelperJ11.INSTANCE;
                case 12:
                    return RepackageHelperJ12.INSTANCE;
                case 13:
                    return RepackageHelperJ13.INSTANCE;
                case 15:
                    return RepackageHelperJ15.INSTANCE;
                case 16:
                    return RepackageHelperJ16.INSTANCE;
            }
        }
    }
}
