package jfallback.java.lang;

// Added in java 9
public class IllegalCallerException extends RuntimeException {
    public IllegalCallerException() {
        super();
    }
    public IllegalCallerException(String s) {
        super(s);
    }
    public IllegalCallerException(Throwable cause) {
        super(cause);
    }
    public IllegalCallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
