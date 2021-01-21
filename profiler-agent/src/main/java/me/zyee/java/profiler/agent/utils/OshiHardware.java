package me.zyee.java.profiler.agent.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import sun.nio.ch.DirectBuffer;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class OshiHardware implements Hardware {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final byte[] data = new byte[512 * (1 << 20)];
    private File bioFile;
    private File nioFile;

    static {
        final Random random = new Random(System.currentTimeMillis());
        random.nextBytes(data);
    }

    @Override
    public long getProcessorFreq() {
        return SYSTEM_INFO.getHardware().getProcessor().getMaxFreq();
    }

    @Override
    public int getLogicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getLogicalProcessorCount();
    }

    @Override
    public int getPhysicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getPhysicalProcessorCount();
    }

    @Override
    public Map<String, Long> getNetIfs() {
        return SYSTEM_INFO.getHardware().getNetworkIFs()
                .stream().collect(Collectors.toMap(NetworkIF::getName, NetworkIF::getSpeed));
    }

    @Override
    public long heapReadSpeed() {
        final ByteBuffer buf = ByteBuffer.wrap(data);
//        buf.flip();
        long start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            buf.get(i);
        }
        long nano = System.nanoTime() - start;
        buf.clear();
        return calMillisBytes(nano);
    }

    @Override
    public long heapWriteSpeed() {
        final ByteBuffer buf = ByteBuffer.allocate(data.length);
        long start = System.nanoTime();
        for (byte datum : data) {
            buf.put(datum);
        }
        long nano = System.nanoTime() - start;
        buf.clear();
        return calMillisBytes(nano);
    }

    private long calMillisBytes(long nano) {
        if (nano > 0) {
            return (long) (((double) data.length) / nano * TimeUnit.MILLISECONDS.toNanos(1));
        }
        return 0;
    }

    @Override
    public long nonHeapReadSpeed() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
        for (byte datum : data) {
            buf.put(datum);
        }
        buf.flip();
        long start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            buf.get(i);
        }
        long nano = System.nanoTime() - start;
        release(buf);
        return calMillisBytes(nano);
    }

    @Override
    public long nonHeapWriteSpeed() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(data.length);
        long start = System.nanoTime();
        for (byte datum : data) {
            buf.put(datum);
        }
        long nano = System.nanoTime() - start;
        release(buf);
        return calMillisBytes(nano);
    }

    @Override
    public long bioReadSpeed() {
        if (null == bioFile) {
            bioWriteSpeed();
        }
        if (null != bioFile) {
            final byte[] buf = new byte[data.length];
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(bioFile))) {
                long start = System.nanoTime();
                is.read(buf);
                long nano = System.nanoTime() - start;
                return calMillisBytes(nano);
            } catch (IOException ignore) {
            }
        }
        return 0;
    }

    @Override
    public long bioWriteSpeed() {
        try {
            bioFile = File.createTempFile("java-profiler", "stream.io");

            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(bioFile))) {
                long start = System.nanoTime();
                os.write(data);
                long nano = System.nanoTime() - start;
                return calMillisBytes(nano);
            }
        } catch (IOException ignore) {
        }
        return 0;
    }

    @Override
    public long nioReadSpeed() {
        if (null == nioFile) {
            nonHeapWriteSpeed();
        }
        if (null != nioFile) {
            try (RandomAccessFile file = new RandomAccessFile(nioFile, "r")) {
                final MappedByteBuffer buf = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, data.length);
                long start = System.nanoTime();
                for (int i = 0; i < data.length; i++) {
                    buf.get(i);
                }
                long nano = System.nanoTime() - start;
                release(buf);
                return calMillisBytes(nano);
            } catch (IOException ignore) {
            }
        }
        return 0;
    }

    @Override
    public long nioWriteSpeed() {
        try {
            nioFile = File.createTempFile("java-profiler", "nio.io");
            try (RandomAccessFile file = new RandomAccessFile(nioFile, "rw")) {
                final MappedByteBuffer buf = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, data.length);
                long start = System.nanoTime();
                for (byte datum : data) {
                    buf.put(datum);
                }
                long nano = System.nanoTime() - start;
                release(buf);
                return calMillisBytes(nano);
            }
        } catch (IOException ignore) {
        }
        return 0;
    }

    @Override
    public void clear() throws IOException {
        if (null != bioFile) {
            bioFile.delete();
        }
        if (null != nioFile) {
            nioFile.delete();
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
