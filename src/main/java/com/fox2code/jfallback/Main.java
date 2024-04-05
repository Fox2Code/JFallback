package com.fox2code.jfallback;

import com.fox2code.jfallback.impl.RepackageHelper;
import com.fox2code.jfallback.impl.RepackageHelperASM;
import com.fox2code.jfallback.impl.WrappedInstrumentation;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;

public class Main {
    private static Instrumentation inst;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        Main.inst = inst;
    }

    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        Main.inst = inst;
    }

    public static void main(String[] args) {
        if (JFallbackClassLoader.compliantSuperParent != null) {
            throw new IllegalStateException("JFallback Main class called in application");
        }
        if (args.length == 0) {
            System.out.println("java -jar JFallback.jar [args...] Program.jar [program argument]...");
            System.out.println("    [--jvm-version <version>]  ->  Java version to spoof");
            System.out.println("    [--ignore-launcher-agent]  ->  Ignore jar Launch agents");
            System.out.println("    [--dont-apply-recursively]  ->  Don't recursively apply JFallback");
            return;
        }
        int eatenArgs = 0;
        boolean ignoreLaunchAgent = false;
        boolean applyRecursively = true;
        parseArgLoop:
        while (true) {
            switch (args[eatenArgs]) {
                case "--":
                    eatenArgs++;
                    /* pass-through */
                default:
                    break parseArgLoop;
                case "--jvm-version": {
                    String spoofJVMVersion = args[eatenArgs + 1];
                    int versionInt;
                    try {
                        versionInt = Integer.parseInt(spoofJVMVersion);
                        if (versionInt >= 9) {
                            spoofJVMVersion = versionInt + ".0.0";
                        } else {
                            spoofJVMVersion = "1." + versionInt + ".0_0";
                        }
                    } catch (Exception ignored) {
                        try {
                            if (spoofJVMVersion.startsWith("1.")) {
                                spoofJVMVersion = spoofJVMVersion.substring(2, 3);
                            } else {
                                int dot = spoofJVMVersion.indexOf(".");
                                if (dot != -1) {
                                    spoofJVMVersion = spoofJVMVersion.substring(0, dot);
                                }
                            }
                            versionInt = Integer.parseInt(spoofJVMVersion);
                        } catch (Exception e) {
                            versionInt = -1;
                        }
                    }
                    if (versionInt > 11) {
                        System.setProperty("jfallback.targetJvmVersion", "" + versionInt);
                    }
                    // We **MUST** call forceInit before spoofing the version
                    RepackageHelper.forceInit();
                    System.setProperty("java.version", spoofJVMVersion);
                    if (versionInt != -1) {
                        System.setProperty("java.specification.version",
                                (versionInt > 8 ? "" : "1.") + versionInt);
                        System.setProperty("java.class.version",
                                RepackageHelperASM.jvmAsmVersionFromVersionInt(versionInt) + ".0");
                    }
                    eatenArgs += 2;
                    break;
                }
                case "--ignore-launcher-agent": {
                    ignoreLaunchAgent = true;
                    eatenArgs++;
                    break;
                }
                case "--dont-apply-recursively": {
                    applyRecursively = false;
                    eatenArgs++;
                    break;
                }
            }
        }
        File program = new File(args[eatenArgs++]).getAbsoluteFile();
        if (!program.exists()) {
            System.out.println("Program doesn't exists");
            return;
        }
        String mainClass;
        String agentClass;
        try (JarFile jarFile = new JarFile(program)) {
            mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
            agentClass = ignoreLaunchAgent ? null :
                    jarFile.getManifest().getMainAttributes().getValue("Launcher-Agent-Class");
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
        String[] newArgs = new String[args.length - eatenArgs];
        System.arraycopy(args, eatenArgs, newArgs, 0, newArgs.length);
        MethodHandle methodHandle;
        try {
            JFallbackClassLoader jFallbackClassLoader = new JFallbackClassLoader(null,
                    new URL[]{Main.class.getProtectionDomain().getCodeSource().getLocation(),
                            program.toURI().toURL()}, ClassLoader.getSystemClassLoader(), applyRecursively);
            JFallbackClassLoader.setCompliantSuperParent(jFallbackClassLoader);
            // Support "Launcher-Agent-Class" for compatibility, CLI only feature
            if (agentClass != null && !agentClass.isEmpty()) {
                try {
                    jFallbackClassLoader.loadClass(mainClass).getDeclaredMethod("agentmain",
                                    String.class, Instrumentation.class).invoke(null, "",
                            new WrappedInstrumentation(inst, jFallbackClassLoader::addURL));
                } catch (ReflectiveOperationException e) {
                    sneakyThrow(e);
                }
            }
            methodHandle = MethodHandles.publicLookup().unreflect(
                    jFallbackClassLoader.loadClass(mainClass).getDeclaredMethod("main", String[].class));
        } catch (MalformedURLException | ReflectiveOperationException e) {
            throw new RuntimeException("Failed to launch the program", e);
        } finally {
            inst = null;
        }
        try {
            methodHandle.invoke((Object) newArgs);
        } catch (Throwable t) {
            sneakyThrow(t);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
