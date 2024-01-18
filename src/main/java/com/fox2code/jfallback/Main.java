package com.fox2code.jfallback;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) {
        if (JFallbackClassLoader.compliantSuperParent != null) {
            throw new IllegalStateException("JFallback Main class called in application");
        }
        if (args.length == 0) {
            System.out.println("java -jar JFallback.jar Program.jar [program argument]...");
            return;
        }
        File program = new File(args[0]).getAbsoluteFile();
        if (!program.exists()) {
            System.out.println("Program doesn't exists");
            return;
        }
        String mainClass;
        try (JarFile jarFile = new JarFile(program)) {
            mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return;
        }
        if (mainClass == null || mainClass.isEmpty()) {
            System.out.println("No main class found for input program");
            return;
        }
        if (mainClass.startsWith("com.fox2code.jfallback.")) {
            System.out.println("JFallback cannot start itself");
            return;
        }
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        MethodHandle methodHandle;
        try {
            JFallbackClassLoader jFallbackClassLoader = new JFallbackClassLoader(new URL[]{Main.class
                    .getProtectionDomain().getCodeSource().getLocation(), program.toURI().toURL()});
            JFallbackClassLoader.setCompliantSuperParent(jFallbackClassLoader);
            methodHandle = MethodHandles.publicLookup().unreflect(
                    jFallbackClassLoader.loadClass(mainClass).getDeclaredMethod("main", String[].class));
        } catch (MalformedURLException | ReflectiveOperationException e) {
            throw new RuntimeException("Failed to launch the program", e);
        }
        try {
            methodHandle.invoke(newArgs);
        } catch (Throwable t) {
            sneakyThrow(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
