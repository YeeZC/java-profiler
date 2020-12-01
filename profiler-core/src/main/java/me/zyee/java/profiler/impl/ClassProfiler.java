package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.BaseProfiler;
import me.zyee.java.profiler.Granularity;

import java.util.concurrent.Future;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public class ClassProfiler extends BaseProfiler {
    public ClassProfiler() {
        super(Granularity.TEST_CASE);
    }

    @Override
    public Future<?> profile(Class<?>... testCases) {
        return null;
    }
}
