package me.zyee.java.profiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfiler implements Profiler {
    private final Granularity granularity;
    private final List<ProfilerListener> prepared = new ArrayList<>();
    private final List<ProfilerListener> before = new ArrayList<>();
    private final List<ProfilerListener> after = new ArrayList<>();
    private final List<ProfilerListener> clean = new ArrayList<>();
    private final List<ProfilerListener> failed = new ArrayList<>();
    private final List<ProfilerListener> finish = new ArrayList<>();

    public BaseProfiler(Granularity granularity) {
        this.granularity = granularity;
    }
}
