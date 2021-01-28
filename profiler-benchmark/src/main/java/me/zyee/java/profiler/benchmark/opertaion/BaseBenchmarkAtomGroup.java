package me.zyee.java.profiler.benchmark.opertaion;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.zyee.java.profiler.benchmark.BenchmarkInfo;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.impl.BaseAtomGroup;
import me.zyee.java.profiler.utils.ExecutingCommand;
import me.zyee.java.profiler.utils.StringHelper;
import me.zyee.java.profiler.utils.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/26
 */
public abstract class BaseBenchmarkAtomGroup extends BaseAtomGroup {
    private final Class<?> benchmarkClass;
    private final AtomicBoolean init = new AtomicBoolean(false);
    protected final Map<Long, AtomOperation> operations = new ConcurrentHashMap<>();

    public BaseBenchmarkAtomGroup(String name, String pattern, Class<?> benchmarkClass) {
        super(name, pattern);
        this.benchmarkClass = benchmarkClass;
    }

    protected void doRunBenchmarkIfNeed() {
        if (init.compareAndSet(false, true)) {
            try {
                List<String> args = new ArrayList<>();
                args.add(Objects.requireNonNull(ProcessUtils.findJava()).getAbsolutePath());

                final ClassLoader classLoader = BaseBenchmarkAtomGroup.class.getClassLoader();
                if (classLoader instanceof URLClassLoader) {
                    args.add("-cp");
                    final URL[] urls = ((URLClassLoader) classLoader).getURLs();
                    final String collect = Arrays.stream(urls).map(url -> {
                        try {
                            return url.toURI().getPath();
                        } catch (URISyntaxException e) {
                            return null;
                        }
                    }).filter(Objects::nonNull)
                    .collect(Collectors.joining(File.pathSeparator));
                    args.add(collect);
                }
                args.add("me.zyee.java.profiler.benchmark.Main");
                args.add(benchmarkClass.getSimpleName());
                System.out.printf("benchmark %s\n", benchmarkClass.getSimpleName());
                final List<String> result = ExecutingCommand.runNative(args.toArray(new String[0]));
                final Stream<BenchmarkInfo> stream = result.stream().map(arg -> StringHelper.fromArgs(arg, BenchmarkInfo.builder()).build());
                build(stream);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    protected abstract void build(Stream<BenchmarkInfo> stream);

}
