package jfallback.java.lang;

public final class CharacterShims {
    // Added in java 11
    public static String toString(int codePoint) {
        return new String(new int[]{codePoint}, 0, 1);
    }
}
