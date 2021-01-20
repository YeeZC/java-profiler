package me.zyee.java.profiler.agent.utils;

import java.util.Map;
import java.util.stream.Collectors;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class OshiHardware implements Hardware {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

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
}
