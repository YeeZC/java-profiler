package me.zyee.profiler.agent.utils;

import java.util.Map;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/19
 */
public interface Hardware {
    long getProcessorFreq();

    int getLogicalProcessorCount();

    int getPhysicalProcessorCount();

    Map<String, Long> getNetIfs();
}
