package jfallback.java.nio.file;

import com.fox2code.jfallback.JrtFileSystemProvider;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Map;

public final class FileSystemsShims {
    // Added in java8 but used to workaround lack of jrt-fs support in java8 jvm
    public static FileSystem newFileSystem(URI uri, Map<String,?> map) throws IOException {
        if (uri.getScheme().equals("jrt")) {
            ClassLoader classLoader = JrtFileSystemProvider.class.getClassLoader();
            if (classLoader != ClassLoader.getSystemClassLoader()) {
                return FileSystems.newFileSystem(uri, map, classLoader);
            }
        }
        return FileSystems.newFileSystem(uri, map);
    }
}
