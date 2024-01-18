package com.fox2code.jfallback.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class RepackageHelper {
    private static final HashSet<String> empty = new HashSet<>();
    private static final int jvmVersion = getJvmVersion0();
    private static final int maxJvmVersion = 11;
    public static final RepackageHelper DEFAULT =
            new RepackageHelper(jvmVersion, maxJvmVersion);

    private static int getJvmVersion0() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }

    final int jvmTarget;
    final int maxBytecodeTarget;
    final HashSet<String> fallbackClasses;
    final HashSet<String> fallbackPackages;
    final HashSet<String> fallbackShims;
    Object asmRemapper;

    public RepackageHelper(int jvmTarget, int maxBytecodeTarget) {
        this.jvmTarget = jvmTarget;
        this.maxBytecodeTarget = maxBytecodeTarget;
        if (jvmTarget >= maxBytecodeTarget) {
            this.fallbackClasses = empty;
            this.fallbackPackages = empty;
            this.fallbackShims = empty;
        } else if (DEFAULT != null && jvmTarget == DEFAULT.jvmTarget &&
                maxBytecodeTarget == DEFAULT.maxBytecodeTarget) {
            this.fallbackClasses = DEFAULT.fallbackClasses;
            this.fallbackPackages = DEFAULT.fallbackPackages;
            this.fallbackShims = DEFAULT.fallbackShims;
            this.asmRemapper = DEFAULT.asmRemapper;
        } else {
            this.fallbackClasses = new HashSet<>();
            this.fallbackPackages = new HashSet<>();
            this.fallbackShims = new HashSet<>();

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
            }
            if (this.jvmTarget >= 9) {
                // On java9 and later, ask mapper to unmap flow
                this.fallbackClasses.add("jfallback/java/util/concurrent/Flow");
            }
            // Assume full class implementation is always up-to-date
            this.fallbackShims.removeIf(k ->
                    this.fallbackClasses.contains(
                            k.substring(0, k.indexOf('.'))));
        }
    }

    public String clsRepackage(String cls) {
        return asmRepackage(cls.replace('.', '/')).replace('/', '.');
    }

    public String asmRepackage(String asm) {
        if (this.fallbackClasses.contains(asm)) {
            if (asm.startsWith("jfallback/")) {
                return asm.substring(10);
            }
            return "jfallback/" + asm;
        }
        return asm;
    }

    abstract static class VersionSpecificLoader {
        Set<String> getVersionSpecificClasses() { return Collections.emptySet(); }

        Set<String> getVersionSpecificPackages() { return Collections.emptySet(); }

        Set<String> getVersionSpecificShims() { return Collections.emptySet(); }

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
            }
        }
    }
}
