package com.fox2code.jfallback.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;

public final class RepackageHelperASM extends Remapper {
    public static final RepackageHelperASM DEFAULT =
            RepackageHelperASM.getFrom(RepackageHelper.DEFAULT);
    public static final RepackageHelperASM RECURSIVE =
            RepackageHelperASM.getFrom(RepackageHelper.RECURSIVE);
    private final RepackageHelper repackageHelper;
    public final int asmClassVersionTarget;
    public final int asmClassVersionMaximum;

    RepackageHelperASM(RepackageHelper repackageHelper) {
        this.repackageHelper = repackageHelper;
        this.asmClassVersionTarget = RepackageHelperASM
                .jvmAsmVersionFromVersionInt(repackageHelper.jvmTarget);
        this.asmClassVersionMaximum = RepackageHelperASM
                .jvmAsmVersionFromVersionInt(repackageHelper.maxBytecodeTarget);
    }

    public RepackageHelper getRepackageHelper() {
        return this.repackageHelper;
    }

    @Override
    public String map(String internalName) {
        return this.repackageHelper.asmRepackage(internalName);
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        return this.repackageHelper.asmRenameMethod(owner, name, descriptor);
    }

    public boolean processForASMVersion(int asmVersion) {
        return this.asmClassVersionTarget < asmVersion &&
                asmVersion <= this.asmClassVersionMaximum;
    }

    public boolean isNoOP() {
        return this.asmClassVersionTarget >= this.asmClassVersionMaximum;
    }

    public boolean shimCall(String owner, String name, String desc) {
        return this.repackageHelper.fallbackShims.contains(owner + "." + name + desc);
    }

    public boolean shimInit(String owner) {
        return this.repackageHelper.fallbackInitShims.contains(owner);
    }

    public static RepackageHelperASM getFrom(RepackageHelper repackageHelper) {
        RepackageHelperASM repackageHelperASM = (RepackageHelperASM) repackageHelper.asmRemapper;
        if (repackageHelperASM == null) {
            repackageHelperASM = new RepackageHelperASM(repackageHelper);
            repackageHelper.asmRemapper = repackageHelperASM;
        }
        return repackageHelperASM;
    }

    public static int jvmAsmVersionFromVersionInt(int jvmVersion) {
        switch (jvmVersion) {
            default:
                throw new IllegalArgumentException("Unsupported version: " + jvmVersion);
            case 3:
                return Opcodes.V1_3;
            case 4:
                return Opcodes.V1_4;
            case 5:
                return Opcodes.V1_5;
            case 6:
                return Opcodes.V1_6;
            case 7:
                return Opcodes.V1_7;
            case 8:
                return Opcodes.V1_8;
            case 9:
                return Opcodes.V9;
            case 10:
                return Opcodes.V10;
            case 11:
                return Opcodes.V11;
            case 12:
                return Opcodes.V12;
            case 13:
                return Opcodes.V13;
            case 14:
                return Opcodes.V14;
            case 15:
                return Opcodes.V15;
            case 16:
                return Opcodes.V16;
            case 17:
                return Opcodes.V17;
            case 18:
                return Opcodes.V18;
            case 19:
                return Opcodes.V19;
            case 20:
                return Opcodes.V20;
            case 21:
                return Opcodes.V21;
            case 22:
                return Opcodes.V22;
        }
    }
}
