package jfallback.java.lang.invoke;

import jfallback.java.lang.constant.Constable;
import jfallback.java.lang.constant.ConstantDesc;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

// Added in java 9
public final class VarHandle implements Constable {
    private final MethodHandle getter, setter;
    private final String finalErrMsg;

    VarHandle(MethodHandle getter, MethodHandle setter, String finalErrMsg) {
        this.getter = getter;
        this.setter = setter;
        this.finalErrMsg = finalErrMsg;
    }

    // Added in java 9
    // Note: VarHandle.get() is @MethodHandle.PolymorphicSignature
    public Object get() {
        try {
            return this.getter.invoke();
        } catch (Throwable t) {
            sneakyThrow(t);
            return null;
        }
    }
    public Object get(Object arg1) {
        try {
            return this.getter.invoke(arg1);
        } catch (Throwable t) {
            sneakyThrow(t);
            return null;
        }
    }
    public Object get(Object arg1, int arg2) {
        try {
            return this.getter.invoke(arg1, arg2);
        } catch (Throwable t) {
            sneakyThrow(t);
            return null;
        }
    }

    // Added in java 9
    // Note: VarHandle.set() is @MethodHandle.PolymorphicSignature
    public void set(Object arg1) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, Object arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, boolean arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, int arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, long arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, float arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, double arg2) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2); } catch (Throwable t) { sneakyThrow(t); }
    }
    public void set(Object arg1, int arg2, Object arg3) {
        if (this.finalErrMsg != null) throw new UnsupportedOperationException(this.finalErrMsg);
        try { this.setter.invoke(arg1, arg2, arg3); } catch (Throwable t) { sneakyThrow(t); }
    }

    // Added in java 9
    // Note: VarHandle.getAndSet() is @MethodHandle.PolymorphicSignature
    public Object getAndSet(Object arg1) {
        Object ret = this.get();
        this.set(arg1);
        return ret;
    }
    public Object getAndSet(Object arg1, Object arg2) {
        Object ret = this.get(arg1);
        this.set(arg1, arg2);
        return ret;
    }
    public Object getAndSet(Object arg1, boolean arg2) {
        Object ret = this.get(arg1);
        this.set(arg1, arg2);
        return ret;
    }

    // Added in java 9
    // Note: VarHandle.getVolatile() is @MethodHandle.PolymorphicSignature
    public Object getVolatile() { return this.get(); }
    public Object getVolatile(Object arg1) { return this.get(arg1); }
    public Object getVolatile(Object arg1, int arg2) { return this.get(arg1, arg2); }

    // Added in java 9
    // Note: VarHandle.setVolatile() is @MethodHandle.PolymorphicSignature
    public void setVolatile(Object arg1) { this.set(arg1); }
    public void setVolatile(Object arg1, Object arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, boolean arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, int arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, long arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, float arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, double arg2) { this.set(arg1, arg2); }
    public void setVolatile(Object arg1, int arg2, Object arg3) { this.set(arg1, arg2, arg3); }

    // Added in java 9
    // Note: VarHandle.getOpaque() is @MethodHandle.PolymorphicSignature
    public Object getOpaque() { return this.get(); }
    public Object getOpaque(Object arg1) { return this.get(arg1); }
    public Object getOpaque(Object arg1, int arg2) { return this.get(arg1, arg2); }

    // Added in java 9
    // Note: VarHandle.setOpaque() is @MethodHandle.PolymorphicSignature
    public void setOpaque(Object arg1) { this.set(arg1); }
    public void setOpaque(Object arg1, Object arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, boolean arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, int arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, long arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, float arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, double arg2) { this.set(arg1, arg2); }
    public void setOpaque(Object arg1, int arg2, Object arg3) { this.set(arg1, arg2, arg3); }


    // Added in java 9
    // Note: VarHandle.getAcquire() is @MethodHandle.PolymorphicSignature
    public Object getAcquire() { return this.get(); }
    public Object getAcquire(Object arg1) { return this.get(arg1); }
    public Object getAcquire(Object arg1, int arg2) { return this.get(arg1, arg2); }

    // Added in java 9
    // Note: VarHandle.setVolatile() is @MethodHandle.PolymorphicSignature
    public void setRelease(Object arg1) { this.set(arg1); }
    public void setRelease(Object arg1, Object arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, boolean arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, int arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, long arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, float arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, double arg2) { this.set(arg1, arg2); }
    public void setRelease(Object arg1, int arg2, Object arg3) { this.set(arg1, arg2, arg3); }

    // Added in java 9
    public void compareAndSet(Object arg1, Object arg2) {
        if (this.get() == arg1) {
            this.set(arg2);
        }
    }
    public void compareAndSet(Object arg1, Object arg2, Object arg3) {
        if (this.get(arg1) == arg2) {
            this.set(arg1, arg3);
        }
    }
    public void compareAndSet(Object arg1, long arg2, long arg3) {
        Object ret;
        if ((ret = this.get(arg1)) instanceof Long && (Long) ret == arg2) {
            this.set(arg1, arg3);
        }
    }

    // Added in java 9
    public Object compareAndExchange(Object arg1, Object arg2) {
        Object ret;
        if ((ret = this.get()) == arg1) {
            this.set(arg2);
        }
        return ret;
    }
    public Object compareAndExchange(Object arg1, Object arg2, Object arg3) {
        Object ret;
        if ((ret = this.get(arg1)) == arg2) {
            this.set(arg1, arg3);
        }
        return ret;
    }
    public Object compareAndExchange(Object arg1, int arg2, int arg3) {
        Object ret;
        if ((ret = this.get(arg1)) instanceof Integer && (Integer) ret == arg2) {
            this.set(arg1, arg3);
        }
        return ret;
    }
    public Object compareAndExchange(Object arg1, long arg2, long arg3) {
        Object ret;
        if ((ret = this.get(arg1)) instanceof Long && (Long) ret == arg2) {
            this.set(arg1, arg3);
        }
        return ret;
    }

    // Added in java 9
    public Object getAndBitwiseOr(Object arg1, int arg2) {
        try {
            return ((int) this.getter.invoke(arg1)) | arg2;
        } catch (Throwable t) {
            sneakyThrow(t);
            return null;
        }
    }

    // Added in java 12
    @Override
    public Optional<? extends ConstantDesc> describeConstable() {
        return Optional.empty();
    }

    // Added in java 16
    public boolean hasInvokeExactBehavior() {
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
