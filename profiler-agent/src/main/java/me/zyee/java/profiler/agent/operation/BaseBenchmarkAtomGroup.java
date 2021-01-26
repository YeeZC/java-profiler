package me.zyee.java.profiler.agent.operation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import me.zyee.java.profiler.agent.benchmark.BenchmarkInfo;
import me.zyee.java.profiler.operation.AtomOperation;
import me.zyee.java.profiler.operation.impl.BaseAtomGroup;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
            final Options build = new OptionsBuilder().include(benchmarkClass.getSimpleName())
                    .build();

            try {
                final Collection<RunResult> run = new Runner(build).run();
                final Stream<BenchmarkInfo> stream = run.stream()
                        .map(BenchmarkInfo.builder()::of)
                        .map(BenchmarkInfo.Builder::build);
                build(stream);
            } catch (RunnerException ignore) {
            }
        }

    }

    protected abstract void build(Stream<BenchmarkInfo> stream);

}
