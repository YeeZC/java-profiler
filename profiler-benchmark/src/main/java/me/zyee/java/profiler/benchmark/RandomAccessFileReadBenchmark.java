package me.zyee.java.profiler.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
public class RandomAccessFileReadBenchmark {
    private File file;
    private byte[] data;
    private FileChannel channel;
    private ByteBuffer buffer;
    private byte current;

    @Setup
    public void init() {
        try {
            file = File.createTempFile("java-profiler.write", ".benchmark");
            data = new byte[128 * (1 << 20)];
            new Random().nextBytes(data);
            ByteBuffer buf;
            try (FileChannel channel = new RandomAccessFile(file, "rw")
                    .getChannel()) {
                buf = channel.map(FileChannel.MapMode.READ_WRITE, 0, data.length);
                for (byte datum : data) {
                    buf.put(datum);
                }
            }
            DirectMemoryReadBenchmark.release(buf);
            channel = new RandomAccessFile(file, "rw").getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public byte[] write() throws IOException {
        for (int i = 0; i < data.length; i++) {
            current = buffer.get(i);
        }
        channel.close();
        return data;
    }

    @TearDown
    public void destroy() {
        if (null != file) {
            file.delete();
        }
        if (null != buffer) {
            DirectMemoryReadBenchmark.release(buffer);
        }
    }
}
