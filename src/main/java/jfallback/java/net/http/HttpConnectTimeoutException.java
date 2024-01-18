package jfallback.java.net.http;

public class HttpConnectTimeoutException extends HttpTimeoutException {
    public HttpConnectTimeoutException(String message) {
        super(message);
    }
}
