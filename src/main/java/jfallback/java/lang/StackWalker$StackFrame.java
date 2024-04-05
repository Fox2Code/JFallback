package jfallback.java.lang;

import java.lang.invoke.MethodType;

// Added in java 9
public interface StackWalker$StackFrame {
    // Added in java 9
    String getClassName();
    // Added in java 9
    String getMethodName();
    // Added in java 9
    Class<?> getDeclaringClass();
    // Added in java 9
    int getByteCodeIndex();
    // Added in java 9
    String getFileName();
    // Added in java 9
    int getLineNumber();
    // Added in java 9
    boolean isNativeMethod();
    // Added in java 9
    StackTraceElement toStackTraceElement();
    // Added in java 10
    default MethodType getMethodType() {
        throw new UnsupportedOperationException();
    }
    // Added in java 10
    default String getDescriptor() {
        throw new UnsupportedOperationException();
    }
}
