package me.zyee.java.profiler.agent.benchmark;

import java.lang.reflect.Field;
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
import sun.misc.Unsafe;

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
public class UnsafeCopyBenchmark {
    @Param({"10", "100", "1000", "10000", "50000"})
    private int length;

    private byte[] data;
    private int start;

    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable cause) {
            unsafe = null;
        }
        UNSAFE = unsafe;
    }

    @Setup
    public void init() {
        Random random = new Random();
        data = new byte[random.nextInt(length) + 1];
        random.nextBytes(data);
        start = random.nextInt(data.length);
    }

    @Benchmark
    public byte[] test() {
        byte[] result = new byte[data.length * 2];
        UNSAFE.copyMemory(data, Unsafe.ARRAY_BYTE_BASE_OFFSET, result, Unsafe.ARRAY_BYTE_BASE_OFFSET + start, data.length);
        return result;
    }

    @TearDown
    public void destroy() {
        data = null;
    }
}
