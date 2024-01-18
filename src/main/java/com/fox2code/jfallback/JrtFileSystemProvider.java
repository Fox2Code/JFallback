package com.fox2code.jfallback;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

/**
 * A fallback jrt-fs implementation in case the current jvm doesn't have one.
 */
public class JrtFileSystemProvider extends FileSystemProvider {
    private static final HashMap<String, WeakReference<JFallbackClassLoader>> JRT_FS_LOADERS = new HashMap<>();

    private FileSystemProvider wrappedFileSystemProvider;

    @Override
    public String getScheme() {
        return "jrt";
    }

    private void checkUri(final URI uri) {
        if (!uri.getScheme().equalsIgnoreCase(this.getScheme())) {
            throw new IllegalArgumentException("URI does not match this provider");
        }
        if (uri.getAuthority() != null) {
            throw new IllegalArgumentException("Authority component present");
        }
        if (uri.getPath() == null) {
            throw new IllegalArgumentException("Path component is undefined");
        }
        if (!uri.getPath().equals("/")) {
            throw new IllegalArgumentException("Path component should be '/'");
        }
        if (uri.getQuery() != null) {
            throw new IllegalArgumentException("Query component present");
        }
        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("Fragment component present");
        }
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        this.checkUri(uri);
        String javaHome = System.getProperty("java.home");
        if (env.containsKey("java.home")) {
            javaHome = Optional.ofNullable((Object)
                    env.get("java.home")).orElse(javaHome).toString();
        }
        if (!new File(javaHome).isAbsolute()) {
            javaHome = new File(javaHome).getAbsolutePath();
        }
        File jrtFs = new File(javaHome, "lib/jrt-fs.jar");
        if (!jrtFs.isFile()) {
            throw new IOException(jrtFs + " does not exist");
        }
        WeakReference<JFallbackClassLoader> fallbackClassLoader = JRT_FS_LOADERS.get(javaHome);
        JFallbackClassLoader jFallbackClassLoader;
        if (fallbackClassLoader == null || (jFallbackClassLoader = fallbackClassLoader.get()) == null) {
            jFallbackClassLoader = new JFallbackClassLoader(new URL[]{jrtFs.toURI().toURL()});
            JRT_FS_LOADERS.put(javaHome, new WeakReference<>(jFallbackClassLoader));
        }
        if (jFallbackClassLoader.jrtFsProvider == null) {
            try {
                jFallbackClassLoader.jrtFsProvider = (FileSystemProvider)
                        jFallbackClassLoader.loadClass(
                                "jdk.internal.jrtfs.JrtFileSystemProvider")
                                .newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IOException(e);
            }
        }
        if (this.wrappedFileSystemProvider == null &&
                javaHome.equals(System.getProperty("java.home"))) {
            this.wrappedFileSystemProvider = jFallbackClassLoader.jrtFsProvider;
        }
        HashMap<String, ?> env2 = new HashMap<>(env);
        env2.remove("java.home");
        return jFallbackClassLoader.jrtFsProvider.newFileSystem(uri, env2);
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        this.checkUri(uri);
        if (this.wrappedFileSystemProvider == null) {
            try {
                // Execute to make it load the jvm file system if possible.
                this.newFileSystem(new URI("jrt:/"), Collections.emptyMap()).close();
            } catch (IOException | URISyntaxException ioe) {
                FileSystemNotFoundException fileSystemNotFoundException =
                        new FileSystemNotFoundException();
                fileSystemNotFoundException.initCause(ioe);
                throw fileSystemNotFoundException;
            }
        }
        return this.wrappedFileSystemProvider.getFileSystem(uri);
    }

    @Override
    public Path getPath(URI uri) {
        this.checkUri(uri);
        return this.wrappedFileSystemProvider.getPath(uri);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return path.getFileSystem().provider().newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return this.wrappedFileSystemProvider.newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        this.wrappedFileSystemProvider.createDirectory(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        this.wrappedFileSystemProvider.delete(path);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        this.wrappedFileSystemProvider.copy(source, target, options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        this.wrappedFileSystemProvider.move(source, target, options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return this.wrappedFileSystemProvider.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return this.wrappedFileSystemProvider.isHidden(path);
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return this.wrappedFileSystemProvider.getFileStore(path);
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        this.wrappedFileSystemProvider.checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return this.wrappedFileSystemProvider.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return this.wrappedFileSystemProvider.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return this.wrappedFileSystemProvider.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        this.wrappedFileSystemProvider.setAttribute(path, attribute, value, options);
    }
}
