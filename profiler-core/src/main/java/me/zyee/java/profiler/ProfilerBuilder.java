package me.zyee.java.profiler;

import me.zyee.java.profiler.impl.ClassProfiler;
import me.zyee.java.profiler.impl.MethodProfiler;
import me.zyee.java.profiler.impl.SuiteProfiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public class ProfilerBuilder implements LifeCycle {
    private Granularity granularity = Granularity.TEST_CASE;
    private final List<ProfilerListener> prepared = new ArrayList<>();
    private final List<ProfilerListener> before = new ArrayList<>();
    private final List<ProfilerListener> after = new ArrayList<>();
    private final List<ProfilerListener> clean = new ArrayList<>();
    private final List<ProfilerListener> failed = new ArrayList<>();
    private final List<ProfilerListener> finish = new ArrayList<>();

    ProfilerBuilder() {
    }

    public ProfilerBuilder granularity(Granularity granularity) {
        this.granularity = granularity;
        return this;
    }

    public Profiler build() {
        BaseProfiler profiler;
        switch (granularity) {
            case SUITE:
                profiler = new SuiteProfiler();
                break;
            case TEST_CASE_METHOD:
                profiler = new MethodProfiler();
                break;
            default:
                profiler = new ClassProfiler();
                break;
        }
        profiler.setPrepared(prepared);
        profiler.setBefore(before);
        profiler.setAfter(after);
        profiler.setClean(clean);
        profiler.setFailed(failed);
        profiler.setFinish(finish);
        return profiler;
    }

    @Override
    public ProfilerBuilder onPrepare(ProfilerListener listener) {
        prepared.add(listener);
        return this;
    }

    @Override
    public ProfilerBuilder onBefore(ProfilerListener listener) {
        before.add(listener);
        return this;
    }

    @Override
    public ProfilerBuilder onAfter(ProfilerListener listener) {
        after.add(listener);
        return this;
    }

    @Override
    public ProfilerBuilder onClean(ProfilerListener listener) {
        clean.add(listener);
        return this;
    }

    @Override
    public ProfilerBuilder onFailed(ProfilerListener listener) {
        failed.add(listener);
        return this;
    }

    @Override
    public ProfilerBuilder onFinished(ProfilerListener listener) {
        finish.add(listener);
        return this;
    }
}
