package jfallback.java.lang.invoke;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Formatter;

public class StringConcatFactory {
    private static final char TAG_ARG = '\u0001';
    private static final char TAG_CONST = '\u0002';
    private static final String TEXT_ARG1 = "\u00011";
    private static final String TEXT_ARG2 = "\u00012";
    private static final String TEXT_ARG3 = "\u00013";
    private static final String TEXT_ARG4 = "\u00014";
    private static final String TEXT_ARG5 = "\u00015";
    private static final String TEXT_ARG6 = "\u00016";
    private static final MethodType SIMPLE1 = MethodType.methodType(
            String.class, Object.class);
    private static final MethodType SIMPLE2 = MethodType.methodType(
            String.class, Object.class, Object.class);
    private static final MethodType SIMPLE3 = MethodType.methodType(
            String.class, Object.class, Object.class, Object.class);
    private static final MethodType SIMPLE4 = MethodType.methodType(
            String.class, Object.class, Object.class, Object.class, Object.class);
    private static final MethodType SIMPLE5 = MethodType.methodType(
            String.class, Object.class, Object.class, Object.class, Object.class, Object.class);
    private static final MethodType SIMPLE6 = MethodType.methodType(
            String.class, Object.class, Object.class, Object.class, Object.class, Object.class);
    private static final MethodHandles.Lookup selfLookup = MethodHandles.lookup();

    public static CallSite makeConcat(MethodHandles.Lookup lookup,
                                      String name,
                                      MethodType concatType) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static CallSite makeConcatWithConstants(
            MethodHandles.Lookup lookup, String name, MethodType concatType,
            String recipe, Object... constants) throws NoSuchMethodException, IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        int prev = 0;
        int args = 0;
        int cst = 0;
        while ((i = nextArg(recipe, prev)) != -1) {
            stringBuilder.append(recipe, prev, i);
            if (recipe.charAt(i) == TAG_CONST) {
                stringBuilder.append(constants[cst]);
                cst++;
            } else {
                args++;
                stringBuilder.append("\u0001").append(args);
            }
            prev = i + 1;
        }
        stringBuilder.append(recipe, prev, recipe.length());
        MethodType methodType;
        switch (args) {
            case 0:
                return new ConstantCallSite(MethodHandles.constant(String.class, stringBuilder.toString()));
            case 1:
                methodType = SIMPLE1;
                break;
            case 2:
                methodType = SIMPLE2;
                break;
            case 3:
                methodType = SIMPLE3;
                break;
            case 4:
                methodType = SIMPLE4;
                break;
            case 5:
                methodType = SIMPLE5;
                break;
            case 6:
                methodType = SIMPLE6;
                break;
            default:
                throw new Error("Only support 6 inputs maximum, failed to make recipe \"" + recipe + "\"");
        }
        return new ConstantCallSite(selfLookup.bind(new SimpleConcatHelper(
                stringBuilder.toString()), "apply", methodType).asType(concatType));
    }

    private static int nextArg(String text, int from) {
        int i = text.indexOf(TAG_ARG, from);
        int i2 = text.indexOf(TAG_CONST, from);
        return i == -1 ? i2 : i2 == -1 ? i : Math.min(i, i2);
    }

    // Performance is very bad, how could I make it better?
    public static final class SimpleConcatHelper {
        private final String pattern;

        public SimpleConcatHelper(String pattern) {
            this.pattern = pattern;
        }

        public String apply(Object o) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o));
        }

        public String apply(Object o1, Object o2) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o1)).replace(TEXT_ARG2, String.valueOf(o2));
        }

        public String apply(Object o1, Object o2, Object o3) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o1)).replace(TEXT_ARG2, String.valueOf(o2))
                    .replace(TEXT_ARG3, String.valueOf(o3));
        }

        public String apply(Object o1, Object o2, Object o3, Object o4) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o1)).replace(TEXT_ARG2, String.valueOf(o2))
                    .replace(TEXT_ARG3, String.valueOf(o3)).replace(TEXT_ARG4, String.valueOf(o4));
        }

        public String apply(Object o1, Object o2, Object o3, Object o4, Object o5) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o1)).replace(TEXT_ARG2, String.valueOf(o2))
                    .replace(TEXT_ARG3, String.valueOf(o3)).replace(TEXT_ARG4, String.valueOf(o4))
                    .replace(TEXT_ARG5, String.valueOf(o5));
        }

        public String apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
            return this.pattern.replace(TEXT_ARG1, String.valueOf(o1)).replace(TEXT_ARG2, String.valueOf(o2))
                    .replace(TEXT_ARG3, String.valueOf(o3)).replace(TEXT_ARG4, String.valueOf(o4))
                    .replace(TEXT_ARG5, String.valueOf(o5)).replace(TEXT_ARG6, String.valueOf(o6));
        }
    }
}
