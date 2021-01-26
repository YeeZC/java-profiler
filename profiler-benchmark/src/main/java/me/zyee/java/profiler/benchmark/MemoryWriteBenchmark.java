package me.zyee.java.profiler.benchmark;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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
public class MemoryWriteBenchmark {

    private byte[] data;
    private ByteBuffer heap;
    private Random random;

    @Setup(Level.Iteration)
    public void init() {
        random = new Random(System.currentTimeMillis());
        data = new byte[10 * (1 << 20)];
        random.nextBytes(data);
        heap = ByteBuffer.wrap(data);
    }


    @Benchmark
    public void heapByteBufferRead() {
        for (int i = 0; i < data.length; i++) {
            heap.put(i, data[i]);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        data = null;
        if (null != heap) {
            heap.clear();
        }
    }


}
