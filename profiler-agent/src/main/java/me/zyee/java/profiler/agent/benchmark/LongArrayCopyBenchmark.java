package me.zyee.java.profiler.agent.benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/25
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 0)
@Fork(1)
@Measurement(iterations = 1, time = 5)
public class LongArrayCopyBenchmark {
    @Param({"10", "100", "1000", "10000", "50000"})
    private int length;

    private long[] data;
    private int start;

    @Setup
    public void init() {
        Random random = new Random();
        data = new long[random.nextInt(length) + 1];
        for (int i = 0; i < length; i++) {
            data[i] = random.nextLong();
        }
        start = random.nextInt(data.length);
    }

    @Benchmark
    public long[] test() {
        long[] result = new long[data.length * 2];
        System.arraycopy(data, 0, result, start, data.length);
        return result;
    }

    @TearDown
    public void destroy() {
        data = null;
    }
}
