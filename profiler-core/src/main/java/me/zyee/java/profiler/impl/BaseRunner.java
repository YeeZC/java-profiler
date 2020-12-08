package me.zyee.java.profiler.impl;

import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.FlameProfiler;
import me.zyee.java.profiler.FlameProfilerProxy;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.Task;
import me.zyee.java.profiler.annotation.Atoms;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/8
 */
public abstract class BaseRunner implements Runner, Task {

    private final FlameProfiler profiler;
    private final Class<?> targetClass;

    public BaseRunner(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.profiler = FlameProfilerProxy.getProfiler();
    }

    @Override
    public Result apply(Context context) {
        if (targetClass.isAnnotationPresent(Atoms.class)) {
            profiler.start();
            try {
                return run();
            } finally {
                profiler.stop();
                profiler.reset();
            }
        }
        return null;
    }
}
