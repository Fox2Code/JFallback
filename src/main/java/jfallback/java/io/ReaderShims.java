package jfallback.java.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public final class ReaderShims {
    // Added in java 10
    public static long transferTo(Reader reader, Writer writer) throws IOException {
        long readLength = 0;
        char[] data = new char[8192];
        int nRead;
        while ((nRead = reader.read(data, 0, data.length)) >= 0) {
            writer.write(data, 0, nRead);
            readLength += nRead;
        }
        return readLength;
    }
}
