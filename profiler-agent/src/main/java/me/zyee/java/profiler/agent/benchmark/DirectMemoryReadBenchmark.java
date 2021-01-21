package me.zyee.java.profiler.agent.benchmark;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
import sun.nio.ch.DirectBuffer;

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
public class DirectMemoryReadBenchmark {
    private byte[] data;
    private ByteBuffer dir;
    private byte current;


    @Setup(Level.Iteration)
    public void init() {
        data = new byte[50 * (1 << 20)];
        new Random().nextBytes(data);
        dir = ByteBuffer.allocateDirect(data.length);
        for (byte datum : data) {
            dir.put(datum);
        }
    }

    @Benchmark
    public void read() {
        for (int i = 0; i < data.length; i++) {
            current = dir.get(i);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        data = null;
        if (null != dir) {
            release(dir);
        }
    }

    public static void release(final ByteBuffer buf) {
        if (buf != null && buf.isDirect()) {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                try {
                    DirectBuffer directBuffer = (DirectBuffer) buf;
                    Method cleanerMethod = (directBuffer).getClass().getMethod("cleaner");
                    cleanerMethod.setAccessible(true);
                    Object cleaner = cleanerMethod.invoke(directBuffer);
                    if (cleaner != null) {
                        cleaner.getClass().getMethod("clean").invoke(cleaner);
                        cleaner.getClass().getMethod("clear").invoke(cleaner);
                    }
                } catch (NoSuchMethodError | Exception ignore) {
                }
                return null;
            });
        }
    }
}
