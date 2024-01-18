package jfallback.java.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class InputStreamShims {
    // Added in java 9
    public static long transferTo(InputStream inputStream, OutputStream out) throws IOException {
        long readLength = 0;
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            out.write(data, 0, nRead);
            readLength += nRead;
        }
        return readLength;
    }


    // Added in java 11
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transferTo(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
