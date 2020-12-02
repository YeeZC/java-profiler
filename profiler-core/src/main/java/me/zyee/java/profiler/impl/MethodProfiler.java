package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.BaseProfiler;
import me.zyee.java.profiler.Granularity;
import me.zyee.java.profiler.Result;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public class MethodProfiler extends BaseProfiler {
    public MethodProfiler() {
        super(Granularity.TEST_CASE_METHOD);
    }

    @Override
    protected Result doProfile(Class<?>... testCases) {
        return null;
    }
}
