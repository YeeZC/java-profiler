package me.zyee.java.profiler.benchmark;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class FileReadBenchmark {
    private File file;
    private byte[] data;

    @Setup
    public void init() {
        try {
            file = File.createTempFile("java-profiler.write", ".benchmark");
            data = new byte[128 * (1 << 20)];
            new Random().nextBytes(data);
            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                os.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public byte[] read() {
        byte[] buf = new byte[data.length];
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            is.read(buf);
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @TearDown
    public void destroy() {
        if (null != file) {
            file.delete();
        }
    }
}
