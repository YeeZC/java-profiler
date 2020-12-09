package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.FlameProfiler;
import me.zyee.java.profiler.FlameProfilerProxy;
import me.zyee.java.profiler.ProfileItem;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/9
 */
public class ContextImpl implements Context {
    private final Queue<ProfileItem> queue = new ConcurrentLinkedQueue<>();

    @Override
    public FlameProfiler getProfiler() {
        return FlameProfilerProxy.getProfiler();
    }

    @Override
    public Queue<ProfileItem> getProfileItems() {
        return queue;
    }
}
