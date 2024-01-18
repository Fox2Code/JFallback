package jfallback.java.net.http;

import java.io.IOException;

public class HttpTimeoutException extends IOException {
    public HttpTimeoutException(String message) {
        super(message);
    }
}
