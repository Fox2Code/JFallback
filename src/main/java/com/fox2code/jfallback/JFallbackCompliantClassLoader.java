package com.fox2code.jfallback;

/**
 * Tells that a class loader is JFallback compliant.
 */
public interface JFallbackCompliantClassLoader {
    default String getName() { return this.getNameJFallback(); }

    default String getNameJFallback() { return null; }
}
