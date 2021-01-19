package me.zyee.profiler.agent.utils;

import java.util.Map;
import java.util.stream.Collectors;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class Hardware {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    public static long getProcessorFreq() {
        return SYSTEM_INFO.getHardware().getProcessor().getMaxFreq();
    }

    public static int getLogicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getLogicalProcessorCount();
    }

    public static int getPhysicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getPhysicalProcessorCount();
    }

    public static Map<String, Long> getNetIfs() {
        return SYSTEM_INFO.getHardware().getNetworkIFs()
                .stream().collect(Collectors.toMap(NetworkIF::getName, NetworkIF::getSpeed));
    }
}
