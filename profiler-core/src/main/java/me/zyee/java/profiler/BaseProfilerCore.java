package me.zyee.java.profiler;

import java.util.concurrent.Future;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfilerCore implements ProfilerCore {

    @Override
    public Future<?> profile(Runner testCases) {
        return null;
    }
}
