package me.zyee.java.profiler.agent.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
class ProfilerClassLoader extends URLClassLoader {

    ProfilerClassLoader(URL[] urls) {
        super(urls, ProfilerClassLoader.class.getClassLoader());
    }

    static ProfilerClassLoader newInstance(String path) throws IOException {
        final Path lib = Paths.get(path);
        final List<String> jars = Files.list(lib).map(Path::toString)
                .filter(p -> p.endsWith("jar"))
                .map(p -> "file:" + p)
                .collect(Collectors.toList());
        List<URL> urls = new ArrayList<>();
        for (String jar : jars) {
            urls.add(new URL(jar));
        }
        final Path agent = lib.getParent().resolve("profiler-agent.jar");
        urls.add(agent.toUri().toURL());
        return new ProfilerClassLoader(urls.toArray(new URL[0]));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            try {
                Class<?> c = findClass(name);
                if (c == null) {
                    c = super.loadClass(name, resolve);
                } else if (resolve) {
                    resolveClass(c);
                }
                return c;
            } catch (Throwable t) {
                return super.loadClass(name, resolve);
            }
        }
    }
}
