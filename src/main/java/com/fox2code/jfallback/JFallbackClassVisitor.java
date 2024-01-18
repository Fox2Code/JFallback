package com.fox2code.jfallback;

import com.fox2code.jfallback.impl.RepackageHelper;
import com.fox2code.jfallback.impl.RepackageHelperASM;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;

public class JFallbackClassVisitor extends ClassRemapper {
    private static final int ASM_API = Opcodes.ASM9;

    private final RepackageHelperASM repackageHelperASM;
    private boolean doNothing = false;
    private boolean jfallback = false;
    private boolean makeLocal = false;
    private String name;

    public JFallbackClassVisitor(ClassVisitor classVisitor) {
        this(classVisitor, RepackageHelperASM.DEFAULT);
    }

    public JFallbackClassVisitor(ClassVisitor classVisitor, int targetJVM, int maxBytecodeTarget) {
        this(classVisitor, RepackageHelperASM.getFrom(new RepackageHelper(targetJVM, maxBytecodeTarget)));
    }

    private JFallbackClassVisitor(ClassVisitor classVisitor, RepackageHelperASM repackageHelperASM) {
        super(ASM_API, classVisitor, repackageHelperASM);
        this.repackageHelperASM = repackageHelperASM;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = name;
        if (this.repackageHelperASM.processForASMVersion(version)) {
            version = this.repackageHelperASM.asmClassVersionTarget;
            this.doNothing = false;
            this.jfallback = false;
            this.makeLocal = false;
        } else if (name.startsWith("jfallback/")) {
            this.doNothing = name.endsWith("Shims");
            this.jfallback = true;
            this.makeLocal = false;
        } else {
            this.doNothing = true;
            this.jfallback = false;
            this.makeLocal = false;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
        if (name.startsWith(this.name)) {
            this.makeLocal = this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9;
        }
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(owner, name, descriptor);
        this.makeLocal = this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (this.makeLocal) {
            access &= ~Opcodes.ACC_PRIVATE;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (this.makeLocal) {
            access &= ~Opcodes.ACC_PRIVATE;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public ModuleVisitor visitModule(String name, int flags, String version) {
        if (this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9) {
            return null;
        }
        return super.visitModule(name, flags, version);
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
        return this.doNothing ? methodVisitor :
                this.jfallback ?
                        super.createMethodRemapper(methodVisitor) :
                new JFallbackMethodVisitor(methodVisitor);
    }

    public boolean didNothing() {
        return this.doNothing;
    }

    private class JFallbackMethodVisitor extends MethodRemapper {
        protected JFallbackMethodVisitor(MethodVisitor methodVisitor) {
            super(ASM_API, methodVisitor, JFallbackClassVisitor.this.repackageHelperASM);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner,
                                    String name, String descriptor, boolean isInterface) {
            if (JFallbackClassVisitor.this.repackageHelperASM.shimCall(owner, name, descriptor)) {
                boolean isStatic = (opcodeAndSource & (~Opcodes.SOURCE_MASK)) == Opcodes.INVOKESTATIC;
                if (isStatic) {
                    super.visitMethodInsn(opcodeAndSource,
                            "jfallback/" + owner + "Shims",
                            name, descriptor, false);
                } else {
                    super.visitMethodInsn(Opcodes.INVOKESTATIC |
                                    (opcodeAndSource & Opcodes.SOURCE_MASK),
                            "jfallback/" + owner + "Shims",
                            name, descriptor.replace("(", "(L" + owner + ";"), false);
                }

            } else super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        }
    }
}
