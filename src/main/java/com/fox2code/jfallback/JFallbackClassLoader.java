package com.fox2code.jfallback;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.spi.FileSystemProvider;
import java.security.CodeSigner;
import java.security.CodeSource;

public class JFallbackClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    /**
     * Parent for JFallbackClassLoader that is JFallback compliant.
     */
    static ClassLoader compliantSuperParent;
    private final boolean hasCompliantParent;
    FileSystemProvider jrtFsProvider;

    public JFallbackClassLoader(URL[] urls) {
        // Uses compliantSuperParent if not null.
        super(urls, compliantSuperParent != null ?
                compliantSuperParent :
                ClassLoader.getSystemClassLoader());
        this.hasCompliantParent = this.hasCompliantParent0();
    }

    public JFallbackClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.hasCompliantParent = this.hasCompliantParent0();
    }

    private boolean hasCompliantParent0() {
        ClassLoader parent = this.getParent();
        return parent instanceof JFallbackClassLoader ||
                parent instanceof JFallbackCompliantClassLoader ||
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
        URL resource = (name.startsWith("jfallback.") ?
                JFallbackClassLoader.class.getClassLoader() : this)
                .getResource(name.replace('.', '/').concat(".class"));
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
            ClassWriter classWriter = new ClassWriter(0);
            JFallbackClassVisitor jFallbackClassVisitor =
                    new JFallbackClassVisitor(classWriter);
            new ClassReader(buffer.toByteArray())
                    .accept(jFallbackClassVisitor, 0);
            if (!jFallbackClassVisitor.didNothing()) {
                bytes = classWriter.toByteArray();
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

    /**
     * Tells that the class loader if JFallback compliant.
     */
    public interface JFallbackCompliantClassLoader {}

    public static void setCompliantSuperParent(ClassLoader compliantSuperParent) {
        if (JFallbackClassLoader.compliantSuperParent != null)
            throw new IllegalStateException("CompliantSuperParent is already set");
        if (!(compliantSuperParent instanceof JFallbackClassLoader ||
                compliantSuperParent instanceof JFallbackCompliantClassLoader ||
                // Allow the class loader that loaded JFallback to be a super parent if it's not a jvm class loader
                (compliantSuperParent == JFallbackClassLoader.class.getClassLoader() &&
                        // A JVM ClassLoader should have the class of their ClassLoader be null.
                        compliantSuperParent.getClass().getClassLoader() != null))) {
            throw new IllegalArgumentException("ClassLoader is not JFallback compliant.");
        }
        JFallbackClassLoader.compliantSuperParent = compliantSuperParent;
    }
}
