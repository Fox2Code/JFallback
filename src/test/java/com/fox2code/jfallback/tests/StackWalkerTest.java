package com.fox2code.jfallback.tests;

import jfallback.java.lang.StackWalker;
import jfallback.java.lang.StackWalker$Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StackWalkerTest {
    private static class GetCallerClassCaller {
        static void testCall(StackWalker stackWalker) {
            StackWalkerTest.testGetCallerClassEntryPoint(stackWalker);
        }
    }

    @Test
    public void testGetCallerClass() {
        GetCallerClassCaller.testCall(StackWalker.getInstance(StackWalker$Option.RETAIN_CLASS_REFERENCE));
    }

    static void testGetCallerClassEntryPoint(StackWalker stackWalker) {
        Assertions.assertEquals(GetCallerClassCaller.class, stackWalker.getCallerClass());
    }
}
