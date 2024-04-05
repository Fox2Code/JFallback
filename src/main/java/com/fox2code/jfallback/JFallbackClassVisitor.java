package com.fox2code.jfallback;

import com.fox2code.jfallback.impl.RepackageHelper;
import com.fox2code.jfallback.impl.RepackageHelperASM;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class JFallbackClassVisitor extends ClassRemapper {
    private static final Type ASM_OBJECT_TYPE = Type.getType(Object.class);
    private static final Type ASM_VOID_TYPE = Type.getType(Void.TYPE);
    private static final int ASM_API = Opcodes.ASM9;

    private final RepackageHelperASM repackageHelperASM;
    private final LinkedHashSet<String> privateMembers;
    private final LinkedHashSet<String> mirrors;
    private boolean doNothing = false;
    private boolean jfallback = false;
    /**
     * Modern java may give private members to package local
     * classes in some circumstances, as legacy JVM don't do that
     * this may result in incompatible bytecode if we don't
     * account for that.
     */
    private boolean makeLocal = false;
    private boolean isInterface = false;
    private boolean needsComputeFrames = false;
    private boolean recursivePatchEx = false;
    private String name;

    public JFallbackClassVisitor(ClassVisitor classVisitor) {
        this(classVisitor, RepackageHelperASM.DEFAULT);
    }

    public JFallbackClassVisitor(ClassVisitor classVisitor, int targetJVM, int maxBytecodeTarget) {
        this(classVisitor, RepackageHelperASM.getFrom(new RepackageHelper(targetJVM, maxBytecodeTarget)));
    }

    public JFallbackClassVisitor(ClassVisitor classVisitor, RepackageHelperASM repackageHelperASM) {
        super(ASM_API, classVisitor, repackageHelperASM);
        this.repackageHelperASM = repackageHelperASM;
        this.mirrors = new LinkedHashSet<>();
        this.privateMembers = new LinkedHashSet<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = name;
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
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
        if ((access & Opcodes.ACC_RECORD) != 0 &&
                this.repackageHelperASM.asmClassVersionTarget < 16) {
            access &= ~Opcodes.ACC_RECORD;
            this.doNothing = false;
        }
        // We handle URLClassLoader elsewhere
        this.recursivePatchEx = RepackageHelper.ASM_CLASS_LOADER.equals(superName) ||
                RepackageHelper.ASM_SECURE_CLASS_LOADER.equals(superName);
        if (this.recursivePatchEx) {
            superName = RepackageHelper.ASM_JFALLBACK_SECURE_CLASS_LOADER;
            this.doNothing = false;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
        if (name.startsWith(this.name) && !this.isInterface && !this.doNothing) {
            this.makeLocal = this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9;
        }
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(owner, name, descriptor);
        if (!this.isInterface && !this.doNothing) {
            this.makeLocal = this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (this.makeLocal) {
            access &= ~Opcodes.ACC_PRIVATE;
        } else if (this.isInterface && (access & Opcodes.ACC_PRIVATE) != 0) {
            this.privateMembers.add(name + descriptor);
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
    public void visitEnd() {
        // Note: Mirrors code doesn't require frame recalculation
        for (String mirror : this.mirrors) {
            int i = mirror.indexOf('(');
            String name = mirror.substring(0, i);
            String desc = mirror.substring(i);
            Type type = Type.getMethodType(desc);
            MethodVisitor methodVisitor = this.visitMethod(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE,
                    "mirror#" + name, desc, null, null);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            int index = 1;
            for (Type arg : type.getArgumentTypes()) {
                methodVisitor.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), index);
                index += arg.getSize();
            }
            methodVisitor.visitMethodInsn(
                    this.privateMembers.contains(mirror) ?
                            Opcodes.INVOKESPECIAL : Opcodes.INVOKEINTERFACE,
                    this.name, name, desc, true);
            Type returnType = type.getReturnType();
            methodVisitor.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
            methodVisitor.visitMaxs(Math.max(index, returnType.getSize()), index);
            methodVisitor.visitEnd();
        }
        super.visitEnd();
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

    public boolean needsComputeFrames() {
        return this.needsComputeFrames;
    }

    private class JFallbackMethodVisitor extends MethodRemapper {
        private boolean suppressNextDup = false;

        protected JFallbackMethodVisitor(MethodVisitor methodVisitor) {
            super(ASM_API, methodVisitor, JFallbackClassVisitor.this.repackageHelperASM);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.NEW && JFallbackClassVisitor.this.repackageHelperASM.shimInit(type)) {
                if (this.suppressNextDup) throw new IllegalStateException("Order of operation error");
                JFallbackClassVisitor.this.needsComputeFrames = true;
                this.suppressNextDup = true;
            } else super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.DUP && this.suppressNextDup) {
                this.suppressNextDup = false;
            } else super.visitInsn(opcode);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner,
                                    String name, String descriptor, boolean isInterface) {
            final int opcode = (opcodeAndSource & (~Opcodes.SOURCE_MASK));
            if (JFallbackClassVisitor.this.recursivePatchEx && opcode == Opcodes.INVOKESPECIAL && (
                    RepackageHelper.ASM_CLASS_LOADER.equals(owner) ||
                            RepackageHelper.ASM_SECURE_CLASS_LOADER.equals(owner))) {
                super.visitMethodInsn(opcodeAndSource,
                        RepackageHelper.ASM_JFALLBACK_SECURE_CLASS_LOADER,
                        name, descriptor, isInterface);
            } else if (name.equals("<init>") && JFallbackClassVisitor.this.repackageHelperASM.shimInit(owner)) {
                super.visitMethodInsn(Opcodes.INVOKESTATIC |
                        (opcodeAndSource & Opcodes.SOURCE_MASK),
                        "jfallback/" + owner + "Shims", "_init",
                        descriptor.replace(")V", ")L" + owner + ";"), false);
                if (this.suppressNextDup) {
                    super.visitInsn(Opcodes.POP);
                    this.suppressNextDup = false;
                }
            } else if (JFallbackClassVisitor.this.repackageHelperASM.shimCall(owner, name, descriptor)) {
                if (opcode == Opcodes.INVOKESTATIC) {
                    super.visitMethodInsn(opcodeAndSource,
                            "jfallback/" + owner + "Shims",
                            name, descriptor, false);
                } else if (!"<init>".equals(name)) {
                    super.visitMethodInsn(Opcodes.INVOKESTATIC |
                                    (opcodeAndSource & Opcodes.SOURCE_MASK),
                            "jfallback/" + owner + "Shims",
                            name, descriptor.replace("(", "(L" + owner + ";"), false);
                } else {
                    // Add special case for "<init>" shims, just indicating removal of last parameter.
                    char c = descriptor.charAt(descriptor.length() - 3);
                    if (c == ';') {
                        descriptor = descriptor.substring(0, descriptor.lastIndexOf('L')) + ")V";
                    } else {
                        descriptor = descriptor.substring(0, descriptor.length() - 3) + ")V";
                    }
                    if (c == 'J' || c == 'D') {
                        super.visitInsn(Opcodes.POP2);
                    } else {
                        super.visitInsn(Opcodes.POP);
                    }
                    super.visitMethodInsn(opcodeAndSource,
                            owner, name, descriptor, false);
                }
            } else if (JFallbackClassVisitor.this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9) {
                // Fixes for pre java 9 jvm that require changes not feasible with the current framework
                if (owner.startsWith("java/nio/") && owner.endsWith("Buffer")
                        && !descriptor.endsWith("Ljava/nio/Buffer;") && "flip".equals(name)) {
                    int i = descriptor.lastIndexOf(')') + 1;
                    super.visitMethodInsn(opcodeAndSource, owner, name,
                            descriptor.substring(0, i) + "Ljava/nio/Buffer;", isInterface);
                    super.visitTypeInsn(Opcodes.CHECKCAST, owner);
                } else if (owner.equals("java/lang/invoke/VarHandle") &&
                        (descriptor.contains("(L") || descriptor.contains("([")) &&
                        (name.startsWith("get") || name.startsWith("set") || name.startsWith("compare"))) {
                    Type type = Type.getMethodType(descriptor);
                    Type[] args = type.getArgumentTypes();
                    for (int i = 0; i < args.length; i++) {
                        int sort = args[i].getSort();
                        if (sort == Type.OBJECT || sort == Type.ARRAY) {
                            args[i] = ASM_OBJECT_TYPE;
                        }
                    }
                    boolean doesReturnValue = name.startsWith("get") || name.endsWith("Exchange");
                    super.visitMethodInsn(opcodeAndSource, owner, name,
                            Type.getMethodDescriptor(doesReturnValue ?
                                    ASM_OBJECT_TYPE : ASM_VOID_TYPE, args), isInterface);
                    Type returnType = type.getReturnType();
                    switch (returnType.getSort()) {
                        default:
                            throw new UnsupportedOperationException(
                                    "Unsupported VarHandle return value: " +
                                            returnType.getDescriptor());
                        case Type.ARRAY: {
                            super.visitTypeInsn(Opcodes.CHECKCAST,
                                    returnType.getDescriptor());
                            break;
                        }
                        case Type.OBJECT: {
                            if (!ASM_OBJECT_TYPE.equals(returnType)) {
                                String returnDescriptor = returnType.getDescriptor();
                                super.visitTypeInsn(Opcodes.CHECKCAST,
                                        returnDescriptor.substring(1,
                                                returnDescriptor.length() - 1));
                            }
                            break;
                        }
                        case Type.BOOLEAN: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Boolean", "booleanValue", "()Z", false);
                            break;
                        }
                        case Type.BYTE: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Byte", "byteValue", "()B", false);
                            break;
                        }
                        case Type.SHORT: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Short", "shortValue", "()S", false);
                            break;
                        }
                        case Type.INT: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Integer", "intValue", "()I", false);
                            break;
                        }
                        case Type.LONG: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Long", "longValue", "()J", false);
                            break;
                        }
                        case Type.FLOAT: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Float", "floatValue", "()F", false);
                            break;
                        }
                        case Type.DOUBLE: {
                            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Double", "doubleValue", "()D", false);
                            break;
                        }
                        case Type.VOID:
                    }
                } else super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
            } else super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitInvokeDynamicInsn(
                String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                Object mod = bootstrapMethodArguments[i];
                if (mod instanceof Handle) {
                    bootstrapMethodArguments[i] = patchHandle((Handle) mod);
                }
            }
            if (JFallbackClassVisitor.this.repackageHelperASM.asmClassVersionTarget < Opcodes.V16 &&
                    JFallbackClassVisitor.this.repackageHelperASM.asmClassVersionMaximum >= Opcodes.V16 &&
                    bootstrapMethodHandle.getOwner().equals("java/lang/runtime/ObjectMethods")) {
                bootstrapMethodHandle = new Handle(bootstrapMethodHandle.getTag(), bootstrapMethodHandle.getOwner(),
                        bootstrapMethodHandle.getName(), bootstrapMethodHandle.getDesc()
                        // Required to support records properly
                        .replace("java/lang/invoke/TypeDescriptor", "java/lang/Object"),
                        bootstrapMethodHandle.isInterface());
            }
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        private Handle patchHandle(Handle mod) {
            Handle handle = mod;
            if (JFallbackClassVisitor.this.repackageHelperASM.asmClassVersionTarget < Opcodes.V9 &&
                    JFallbackClassVisitor.this.isInterface && handle.getTag() == Opcodes.H_INVOKEINTERFACE &&
                    handle.getOwner().equals(JFallbackClassVisitor.this.name)) {
                // For java 8 H_INVOKEINTERFACE is not supported for private members,
                // H_INVOKESPECIAL should be used instead for the code to run java8
                if (handle.getName().startsWith("lambda$") ||
                        handle.getName().startsWith("mirror#")) {
                    handle = new Handle(Opcodes.H_INVOKESPECIAL,
                            handle.getOwner(), handle.getName(), handle.getDesc(), true);
                } else {
                    // If we are unsure what to do, make a mirror as it is more reliable
                    JFallbackClassVisitor.this.mirrors.add(handle.getName() + handle.getDesc());
                    handle = new Handle(Opcodes.H_INVOKESPECIAL, handle.getOwner(),
                            "mirror#" + handle.getName(), handle.getDesc(), true);
                }
            } else if (JFallbackClassVisitor.this.repackageHelperASM.shimCall(
                    handle.getOwner(), handle.getName(), handle.getDesc())) {
                // Shims lambdas
                if (handle.getTag() == Opcodes.H_INVOKESTATIC) {
                    handle = new Handle(Opcodes.H_INVOKESTATIC,
                            "jfallback/" + handle.getOwner() + "Shims",
                            handle.getName(), handle.getDesc(), true);
                } else {
                    handle = new Handle(Opcodes.H_INVOKESTATIC,
                            "jfallback/" + handle.getOwner() + "Shims",
                            handle.getName(), handle.getDesc().replace(
                                    "(", "(L" + handle.getOwner() + ";"), true);
                }
            }
            return handle;
        }

        @Override
        public void visitEnd() {
            if (this.suppressNextDup) {
                throw new IllegalStateException("Order of operation error");
            }
            super.visitEnd();
        }
    }
}
