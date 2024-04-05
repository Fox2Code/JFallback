package jfallback.java.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

// Added in java 9
public final class StackWalker {
    private static final class CTXHelper extends SecurityManager {
        private static final CTXHelper INSTANCE = new CTXHelper();

        static Class<?>[] classContext() {
            return INSTANCE.getClassContext();
        }
    }

    private static final class StackFrameImpl implements StackWalker$StackFrame {
        private final StackTraceElement stackTraceElement;
        private final Class<?> classReference;

        StackFrameImpl(StackTraceElement stackTraceElement, Class<?> classReference) {
            this.stackTraceElement = stackTraceElement;
            this.classReference = classReference;
        }

        @Override
        public String getClassName() {
            return this.stackTraceElement.getClassName();
        }

        @Override
        public String getMethodName() {
            return this.stackTraceElement.getMethodName();
        }

        @Override
        public Class<?> getDeclaringClass() {
            if (this.classReference == null)
                throw new UnsupportedOperationException();
            return this.classReference;
        }

        @Override
        public int getByteCodeIndex() {
            return -1;
        }

        @Override
        public String getFileName() {
            return this.stackTraceElement.getFileName();
        }

        @Override
        public int getLineNumber() {
            return this.stackTraceElement.getLineNumber();
        }

        @Override
        public boolean isNativeMethod() {
            return this.stackTraceElement.isNativeMethod();
        }

        @Override
        public StackTraceElement toStackTraceElement() {
            return this.stackTraceElement;
        }
    }

    private static final StackWalker INSTANCE = new StackWalker(
            EnumSet.noneOf(StackWalker$Option.class));

    private final boolean retainClassReference;
    private final boolean showReflectFrames;

    private StackWalker(Set<StackWalker$Option> options) {
        this.retainClassReference = options.contains(
                StackWalker$Option.RETAIN_CLASS_REFERENCE);
        this.showReflectFrames = // SHOW_HIDDEN_FRAMES implies SHOW_REFLECT_FRAMES
                options.contains(StackWalker$Option.SHOW_REFLECT_FRAMES) ||
                options.contains(StackWalker$Option.SHOW_HIDDEN_FRAMES);
    }

    public static StackWalker getInstance() {
        return INSTANCE;
    }

    public static StackWalker getInstance(StackWalker$Option option) {
        return new StackWalker(EnumSet.of(Objects.requireNonNull(option)));
    }

    public static StackWalker getInstance(Set<StackWalker$Option> options) {
        return options.isEmpty() ? INSTANCE : new StackWalker(options);
    }

    public static StackWalker getInstance(Set<StackWalker$Option> options, int i) {
        if (i <= 0) throw new IllegalArgumentException();
        return options.isEmpty() ? INSTANCE : new StackWalker(options);
    }

    private static boolean isReflectionClassName(String className) {
        return className.startsWith("java.lang.invoke.") ||
                className.startsWith("java.lang.invoke.LambdaForm") ||
                className.equals("java.lang.Method") ||
                className.equals("java.lang.Constructor") ||
                className.startsWith("jdk.internal.reflect.") ||
                className.startsWith("sun.reflect.");
    }

    private Stream<StackWalker$StackFrame> buildCallStack() {
        HashMap<String, Class<?>> references = null;
        if (this.retainClassReference) {
            references = new HashMap<>();
            for (Class<?> cls : CTXHelper.classContext()) {
                references.putIfAbsent(cls.getName(), cls);
            }
        }
        StackTraceElement[] stackTraceElements =
                new Throwable().getStackTrace();
        Stream.Builder<StackWalker$StackFrame> stackFrames = Stream.builder();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            final String className = stackTraceElement.getClassName();
            if (isReflectionClassName(className) && !this.showReflectFrames) {
                continue;
            }
            stackFrames.add(new StackFrameImpl(stackTraceElement, references != null ?
                    // Fallback to Object if we failed to get a reference with retainClassReference
                    references.getOrDefault(className, Object.class) : null));
        }
        return stackFrames.build();
    }

    public <T> T walk(Function<? super Stream<StackWalker$StackFrame>, ? extends T> function) {
        Objects.requireNonNull(function, "function");
        return function.apply(this.buildCallStack());
    }

    public void forEach(Consumer<? super StackWalker$StackFrame> action) {
        this.buildCallStack().forEach(action);
    }

    public Class<?> getCallerClass() {
        if (!this.retainClassReference) {
            throw new UnsupportedOperationException();
        }
        Class<?>[] classStack = CTXHelper.classContext();
        for (int i = 3; i < classStack.length; i++) {
            Class<?> caller = classStack[i];
            if (this.showReflectFrames) return caller;
            String callerName = caller.getName();
            if (callerName.startsWith("java.lang.invoke.") ||
                    callerName.startsWith("java.lang.invoke.LambdaForm") ||
                    caller == Method.class || caller == Constructor.class ||
                    callerName.startsWith("jdk.internal.reflect.") ||
                    callerName.startsWith("sun.reflect.")) {
                continue;
            }
            return caller;
        }
        throw new IllegalCallerException();
    }
}
