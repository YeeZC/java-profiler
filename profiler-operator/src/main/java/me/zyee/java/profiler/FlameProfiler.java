package me.zyee.java.profiler;

import java.nio.file.Path;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public interface FlameProfiler {
    void start();

    Path stop();

    void setInclude(String include);

    void setExclude(String exclude);

    void setOutput(String exclude);

    void reset();
}
