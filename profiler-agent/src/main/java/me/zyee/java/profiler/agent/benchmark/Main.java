package me.zyee.java.profiler.agent.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/21
 */
public class Main {
    public static void main(String[] args) throws RunnerException, InterruptedException {
        final Options build = new OptionsBuilder()
//                .include(UnsafeCopyBenchmark.class.getName())
                .include(ByteArrayCopyBenchmark.class.getName())
//                .include(IntArrayCopyBenchmark.class.getName())
//                .include(LongArrayCopyBenchmark.class.getName())
//                .include(DoubleArrayCopyBenchmark.class.getName())
                .build();
        new Runner(build).run();
    }
}
