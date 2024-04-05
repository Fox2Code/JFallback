package com.fox2code.jfallback;

import com.fox2code.jfallback.impl.RepackageHelperASM;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.spi.FileSystemProvider;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Objects;

public class JFallbackClassLoader extends URLClassLoader implements JFallbackCompliantClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public static ClassLoader getCompliantSuperParent() {
        return JFallbackClassLoader.compliantSuperParent;
    }

    public static void setCompliantSuperParent(ClassLoader compliantSuperParent) {
        if (JFallbackClassLoader.compliantSuperParent != null)
            throw new IllegalStateException("CompliantSuperParent is already set");
        if (!(compliantSuperParent instanceof JFallbackCompliantClassLoader ||
                // Allow the class loader that loaded JFallback to be a super parent if it's not a jvm class loader
                (compliantSuperParent == JFallbackClassLoader.class.getClassLoader() &&
                        // A JVM ClassLoader should have the class of their ClassLoader be null.
                        compliantSuperParent.getClass().getClassLoader() != null))) {
            throw new IllegalArgumentException("ClassLoader is not JFallback compliant.");
        }
        JFallbackClassLoader.compliantSuperParent = compliantSuperParent;
    }

    /**
     * Parent for JFallbackClassLoader that is JFallback compliant.
     */
    static ClassLoader compliantSuperParent;
    private final boolean hasCompliantParent;
    private final RepackageHelperASM repackageHelperASM;
    private final String name;
    FileSystemProvider jrtFsProvider;

    public JFallbackClassLoader(URL[] urls) {
        // Uses compliantSuperParent if not null.
        this(null, urls, null, true);
    }

    public JFallbackClassLoader(String name, URL[] urls) {
        this(name, urls, null, true);
    }

    public JFallbackClassLoader(URL[] urls, ClassLoader parent) {
        this(null, urls, parent, true);
    }

    public JFallbackClassLoader(String name, URL[] urls, ClassLoader parent) {
        this(name, urls, parent, true);
    }

    public JFallbackClassLoader(String name, URL[] urls, ClassLoader parent, boolean applyRecursively) {
        super(urls, parent != null ? parent :
                compliantSuperParent != null ? compliantSuperParent :
                ClassLoader.getSystemClassLoader());
        this.hasCompliantParent = this.hasCompliantParent0();
        this.repackageHelperASM = applyRecursively ?
                RepackageHelperASM.RECURSIVE : RepackageHelperASM.DEFAULT;
        this.name = name;
    }

    private boolean hasCompliantParent0() {
        ClassLoader parent = this.getParent();
        return parent instanceof JFallbackCompliantClassLoader ||
                // We may allow the class loader who loaded us to be super parent
                (parent != null && parent == compliantSuperParent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Load JFallback classes in our own loader, unless parent is JFallback compliant.
        if (name.startsWith("jfallback.") && !this.hasCompliantParent) {
            Class<?> cls = this.findLoadedClass(name);
            if (cls != null) return cls;
            return findClass(name);
        }
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // Use class loader that loaded JFallbackClassLoader to load classes
        URL resource = getClassResource(name);
        if (resource == null) throw new ClassNotFoundException(name);
        final String packageName = name.lastIndexOf('.') == -1 ? "" :
                name.substring(0, name.lastIndexOf('.'));
        if (getPackage(packageName) == null) {
            definePackage(packageName, null, null, null, null, null, null, null);
        }
        try {
            URLConnection urlConnection = resource.openConnection();
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] bytes = buffer.toByteArray();
            byte[] patchedBytes = patchClass(new ClassReader(bytes));
            if (patchedBytes != null) bytes = patchedBytes;

            if ("com.destroystokyo.paper.event.executor.asm.SafeClassDefiner$GeneratedClassLoader".equals(name)) {
                Files.write(new File("SafeClassDefiner$GeneratedClassLoader").toPath(), bytes);
            }

            URL url = null;
            if (urlConnection instanceof JarURLConnection) {
                url = ((JarURLConnection) urlConnection).getJarFileURL();
            }

            return defineClass(name,bytes,0,bytes.length, url == null ?
                    null : new CodeSource(url, new CodeSigner[]{}));
        } catch (IOException ioe) {
            throw new ClassNotFoundException(name, ioe);
        }
    }

    protected URL getClassResource(String name) {
        String path = name.replace('.', '/').concat(".class");
        if (name.startsWith("jfallback.")) {
            return JFallbackClassLoader.class.getClassLoader().getResource(path);
        }
        URL resource;
        for (String subPaths : this.repackageHelperASM
                .getRepackageHelper().getClassLoaderSubLoadingPaths()) {
            resource = this.getResource(subPaths + path);
            if (resource != null) return resource;
        }
        return this.getResource(path);
    }

    protected byte[] patchClass(ClassReader classReader) {
        return patchClass(this.repackageHelperASM, classReader);
    }
    protected static byte[] patchClass(RepackageHelperASM repackageHelperASM, ClassReader classReader) {
        ClassWriter classWriter = new ClassWriter(0);
        JFallbackClassVisitor jFallbackClassVisitor =
                new JFallbackClassVisitor(classWriter, repackageHelperASM);
        classReader.accept(jFallbackClassVisitor, 0);
        if (jFallbackClassVisitor.didNothing()) {
            return null;
        }
        byte[] data = classWriter.toByteArray();
        // COMPUTE_FRAMES can break bytecode or fail horribly, avoid it at all costs unless absolutely necessary
        if (!jFallbackClassVisitor.needsComputeFrames()) {
            return data;
        }
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                if (Objects.equals(type1, type2)) return type1;
                if ("java/lang/Object".equals(type1) ||
                        "java/lang/Object".equals(type2)) {
                    return "java/lang/Object";
                }
                return super.getCommonSuperClass(type1, type2);
            }
        };
        new ClassReader(data).accept(classWriter, ClassReader.SKIP_FRAMES);
        return classWriter.toByteArray();
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public final String getNameJFallback() {
        return this.name;
    }

    // Used to apply recursively too
    protected final Class<?> defineClassJFallback(
            String name, byte[] data, int off, int len) {
        byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
        if (patchedBytes != null) {
            return this.defineClass(name, patchedBytes, 0, patchedBytes.length);
        } else {
            return this.defineClass(name, data, off, len);
        }
    }

    protected final Class<?> defineClassJFallback(
            String name, byte[] data, int off, int len, CodeSource codeSource) {
        byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
        if (patchedBytes != null) {
            return this.defineClass(name, patchedBytes, 0, patchedBytes.length, codeSource);
        } else {
            return this.defineClass(name, data, off, len, codeSource);
        }
    }

    protected final Class<?> defineClassJFallback(
            String name, byte[] data, int off, int len, ProtectionDomain protectionDomain) {
        byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
        if (patchedBytes != null) {
            return this.defineClass(name, patchedBytes, 0, patchedBytes.length, protectionDomain);
        } else {
            return this.defineClass(name, data, off, len, protectionDomain);
        }
    }

    public static class JFallbackSecureClassLoader extends SecureClassLoader implements JFallbackCompliantClassLoader {
        private final String name;

        protected JFallbackSecureClassLoader() {
            this(null, null);
        }
        protected JFallbackSecureClassLoader(String name) {
            this(name, null);
        }
        protected JFallbackSecureClassLoader(ClassLoader parent) {
            this(null, parent);
        }
        protected JFallbackSecureClassLoader(String name, ClassLoader parent) {
            super(parent != null ? parent :
                    compliantSuperParent != null ? compliantSuperParent :
                            ClassLoader.getSystemClassLoader());
            this.name = name;
        }

        @Override
        public String getNameJFallback() {
            return this.name;
        }

        protected byte[] patchClass(ClassReader classReader) {
            return JFallbackClassLoader.patchClass(RepackageHelperASM.RECURSIVE, classReader);
        }

        // Used to apply recursively too
        protected final Class<?> defineClassJFallback(
                String name, byte[] data, int off, int len) {
            byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
            if (patchedBytes != null) {
                return this.defineClass(name, patchedBytes, 0, patchedBytes.length);
            } else {
                return this.defineClass(name, data, off, len);
            }
        }

        protected final Class<?> defineClassJFallback(
                String name, byte[] data, int off, int len, CodeSource codeSource) {
            byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
            if (patchedBytes != null) {
                return this.defineClass(name, patchedBytes, 0, patchedBytes.length, codeSource);
            } else {
                return this.defineClass(name, data, off, len, codeSource);
            }
        }

        protected final Class<?> defineClassJFallback(
                String name, byte[] data, int off, int len, ProtectionDomain protectionDomain) {
            byte[] patchedBytes = this.patchClass(new ClassReader(data, off, len));
            if (patchedBytes != null) {
                return this.defineClass(name, patchedBytes, 0, patchedBytes.length, protectionDomain);
            } else {
                return this.defineClass(name, data, off, len, protectionDomain);
            }
        }
    }
}
