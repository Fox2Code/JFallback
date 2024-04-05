package jfallback.java.lang;

public final class StackTraceElementShims {
    // Added in java 9
    public static String getModuleName(StackTraceElement stackTraceElement) {
        return null;
    }
    // Added in java 9
    public static String getModuleVersion(StackTraceElement stackTraceElement) {
        return null;
    }
    // Added in java 9
    public static String getClassLoaderName(StackTraceElement stackTraceElement) {
        return null;
    }

    // Added in java5 (Init shims needs to be handled specially)
    public static StackTraceElement _init(
            String declaringClass, String methodName,
            String fileName, int lineNumber) {
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    // Added in java9
    public static StackTraceElement _init(
            String classLoaderName,
            String moduleName, String moduleVersion,
            String declaringClass, String methodName,
            String fileName, int lineNumber) {
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}
