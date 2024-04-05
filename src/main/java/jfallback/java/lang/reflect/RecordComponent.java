package jfallback.java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Objects;

// Added in java 16
public final class RecordComponent implements AnnotatedElement {
    private final WeakReference<Method> method;
    private final String name;
    private final String toString;

    public RecordComponent(Method method) {
        this.method = new WeakReference<>(method);
        this.name = method.getName();
        this.toString = method.getReturnType().getTypeName() + " " + this.name;
    }

    private Method getMethod() {
        return Objects.requireNonNull(this.method.get());
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.getMethod().getReturnType();
    }

    public String getGenericSignature() {
        return this.getMethod().getReturnType().toGenericString();
    }

    public Method getAccessor() {
        return this.getMethod();
    }

    public AnnotatedType getAnnotatedType() {
        return this.getMethod().getAnnotatedReturnType();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.getMethod().isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return this.getMethod().getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.getMethod().getAnnotations();
    }

    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return this.getMethod().getAnnotationsByType(annotationClass);
    }

    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return this.getMethod().getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return this.getMethod().getDeclaredAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getMethod().getDeclaredAnnotations();
    }

    public String toString() {
        return this.toString;
    }

    public Class<?> getDeclaringRecord() {
        return this.getMethod().getDeclaringClass();
    }
}
