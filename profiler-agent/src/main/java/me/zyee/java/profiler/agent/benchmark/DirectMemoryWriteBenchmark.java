package me.zyee.java.profiler.agent.benchmark;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/21
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 0)
@Fork(1)
@Measurement(iterations = 1, time = 5)
public class DirectMemoryWriteBenchmark {
    private byte[] data;
    private ByteBuffer dir;


    @Setup
    public void init() {
        data = new byte[50 * (1 << 20)];
        new Random().nextBytes(data);
        dir = ByteBuffer.allocateDirect(data.length);
    }

    @Benchmark
    public void read() {
        for (int i = 0; i < data.length; i++) {
            dir.put(i, data[i]);
        }
    }

    @TearDown
    public void tearDown() {
        data = null;
        if (null != dir) {
            DirectMemoryReadBenchmark.release(dir);
        }
    }
}
