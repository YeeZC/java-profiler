package me.zyee.java.profiler.agent.hardware;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/19
 */
public interface Hardware {
    long getProcessorFreq();

    String getProcessorVendor();

    int getLogicalProcessorCount();

    int getPhysicalProcessorCount();

    Map<String, Long> getNetIfs();

    List<String> getMemories();
}
