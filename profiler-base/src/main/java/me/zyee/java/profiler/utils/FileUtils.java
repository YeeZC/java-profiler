package me.zyee.java.profiler.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/8/23
 */
public class FileUtils {
    private static final int PARTITION_SIZE = 1 << 22;

    public static void transfer(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[PARTITION_SIZE];
        int read = -1;
        while ((read = is.read(buffer)) > 0) {
            os.write(buffer, 0, read);
        }
    }

    public static byte[] readAll(InputStream is) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            transfer(is, bos);
            return bos.toByteArray();
        }
    }

    public static String read(InputStream is, Charset charset) throws IOException {
        return new String(readAll(is), charset);
    }
}
