package me.zyee.java.profiler.agent.utils;

import java.io.IOException;
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

    long heapReadSpeed();

    long heapWriteSpeed();

    long nonHeapReadSpeed();

    long nonHeapWriteSpeed();

    long bioReadSpeed();

    long bioWriteSpeed();

    long nioReadSpeed();

    long nioWriteSpeed();

    void clear() throws IOException;
}
