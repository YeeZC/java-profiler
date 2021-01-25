package me.zyee.java.profiler.agent.benchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
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
public class ByteArrayCopyBenchmark {
    @Param({"10", "100", "1000", "10000", "50000"})
    private int length;

    private byte[] data;
    private int start;
    private final Random random = new Random();
    private int copy;

    @Setup(Level.Invocation)
    public void init() {
        data = new byte[length];
        random.nextBytes(data);
        start = random.nextInt(data.length);
        copy = Math.max(length / 10, random.nextInt(length));
    }

    @Benchmark
    public byte[] test() {
        byte[] result = new byte[data.length * 2];
        System.arraycopy(data, 0, result, start, copy);
        return result;
    }

    @Setup(Level.Invocation)
    public void destroy() {
        data = null;
    }

}
