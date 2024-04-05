package com.fox2code.jfallback.impl;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public final class WrappedInstrumentation implements Instrumentation {
    private final Instrumentation wrapped;
    private final Consumer<URL> classLoader;

    public WrappedInstrumentation(Instrumentation wrapped, Consumer<URL> classLoader) {
        this.wrapped = wrapped;
        this.classLoader = classLoader;
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
        if (this.wrapped == null) return;
        this.wrapped.addTransformer(transformer, canRetransform);
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        if (this.wrapped == null) return;
        this.wrapped.addTransformer(transformer);
    }

    @Override
    public boolean removeTransformer(ClassFileTransformer transformer) {
        return this.wrapped != null && this.wrapped.removeTransformer(transformer);
    }

    @Override
    public boolean isRetransformClassesSupported() {
        return this.wrapped != null && this.wrapped.isRetransformClassesSupported();
    }

    @Override
    public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
        this.wrapped.retransformClasses(classes);
    }

    @Override
    public boolean isRedefineClassesSupported() {
        return this.wrapped != null && this.wrapped.isRedefineClassesSupported();
    }

    @Override
    public void redefineClasses(ClassDefinition... definitions)
            throws ClassNotFoundException, UnmodifiableClassException {
        if (this.wrapped == null)
            throw new UnsupportedOperationException();
        this.wrapped.redefineClasses(definitions);
    }

    @Override
    public boolean isModifiableClass(Class<?> theClass) {
        return this.wrapped != null && this.wrapped.isModifiableClass(theClass);
    }

    @Override
    public Class<?>[] getAllLoadedClasses() {
        return this.wrapped.getAllLoadedClasses();
    }

    @Override
    public Class<?>[] getInitiatedClasses(ClassLoader loader) {
        return this.wrapped.getInitiatedClasses(loader);
    }

    @Override
    public long getObjectSize(Object objectToSize) {
        return this.wrapped.getObjectSize(objectToSize);
    }

    @Override
    public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
        this.wrapped.appendToBootstrapClassLoaderSearch(jarfile);
    }

    @Override
    public void appendToSystemClassLoaderSearch(JarFile jarfile) {
        if (this.classLoader != null) {
            try {
                URL url = new File(jarfile.getName()).getAbsoluteFile().toURI().toURL();
                try {
                    jarfile.close();
                } catch (IOException ignored) {}
                this.classLoader.accept(url);
            } catch (MalformedURLException e) {
                throw new UnsupportedOperationException(e);
            }
        } else if (this.wrapped != null) {
            this.wrapped.appendToSystemClassLoaderSearch(jarfile);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isNativeMethodPrefixSupported() {
        return this.wrapped != null && this.wrapped.isNativeMethodPrefixSupported();
    }

    @Override
    public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
        if (this.wrapped == null)
            throw new UnsupportedOperationException();
        this.wrapped.setNativeMethodPrefix(transformer, prefix);
    }
}
