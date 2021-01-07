package me.zyee.java.profiler.utils;

import java.lang.management.ManagementFactory;

/**
 * @author yee
 * @version 1.0
 * Created by yee on 2020/2/12
 */
public class PidUtils {
    private static String PID = "-1";

    static {
        // https://stackoverflow.com/a/7690178
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf('@');

        if (index > 0) {
            try {
                PID = Long.toString(Long.parseLong(jvmName.substring(0, index)));
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    private PidUtils() {
    }

    public static String currentPid() {
        return PID;
    }
}
