package com.fox2code.jfallback.tests;

import jfallback.java.lang.invoke.MethodHandles$LookupShims;
import jfallback.java.lang.invoke.VarHandle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

public class VarHandleTest {
    public static final Boolean aBoolean = Boolean.TRUE;

    @Test
    public void testVarHandleStaticGet() throws NoSuchFieldException, IllegalAccessException {
        VarHandle varHandle = MethodHandles$LookupShims
                .findStaticVarHandle(MethodHandles.lookup(),
                        VarHandleTest.class, "aBoolean", Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, varHandle.get());
    }
}
