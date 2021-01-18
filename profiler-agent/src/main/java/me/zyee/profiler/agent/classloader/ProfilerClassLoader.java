package me.zyee.profiler.agent.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/18
 */
public class ProfilerClassLoader extends URLClassLoader {
    ProfilerClassLoader(URL[] urls) {
        super(urls);
    }

    public static ProfilerClassLoader create(Path path) throws IOException {
        final List<String> collect = Files.list(path).map(Path::toString)
                .filter(p -> p.endsWith(".jar"))
                .map(p -> "file:" + p)
                .collect(Collectors.toList());
        List<URL> urls = new ArrayList<>();
        for (String url : collect) {
            urls.add(new URL(url));
        }
        urls.add(new URL("file:" + path.getParent().toString()));
        return new ProfilerClassLoader(urls.toArray(new URL[0]));
    }
}
