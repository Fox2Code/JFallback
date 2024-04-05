package jfallback.java.nio.file;

import com.fox2code.jfallback.JrtFileSystemProvider;

import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

public final class PathShims {
    // Added in java 11
    public static Path of(String first, String... more) {
        return FileSystems.getDefault().getPath(first, more);
    }

    // Added in java 11
    public static Path of(URI uri) {
        final String scheme =  uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("Missing scheme");
        }

        if (scheme.equalsIgnoreCase("file")) {
            return FileSystems.getDefault().provider().getPath(uri);
        }

        for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
            if (provider.getScheme().equalsIgnoreCase(scheme)) {
                return provider.getPath(uri);
            }
        }

        if (scheme.equals("jrt")) {
            return JrtFileSystemProvider.getEphemeralInstance().getPath(uri);
        }

        throw new FileSystemNotFoundException("Provider \"" + scheme + "\" not installed");
    }
}
