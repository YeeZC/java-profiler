package me.zyee.profiler.agent.loader;

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
public class ProfilerClassLoader extends URLClassLoader {
    private static ProfilerClassLoader instance;

    ProfilerClassLoader(URL[] urls) {
        super(urls, null);
    }

    public synchronized static ProfilerClassLoader create(String path) throws IOException {
        if (null == instance) {
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
            instance = new ProfilerClassLoader(urls.toArray(new URL[0]));
        }
        return instance;
    }

    public static ClassLoader getInstance() {
        return instance;
    }
}
