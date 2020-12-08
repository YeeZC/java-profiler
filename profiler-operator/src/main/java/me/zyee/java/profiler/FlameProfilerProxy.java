package me.zyee.java.profiler;

import java.nio.file.Path;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class FlameProfilerProxy implements FlameProfiler {

    private final FlameProfiler delegate;

    private FlameProfilerProxy() {
        FlameProfiler profiler;
        if (OS.getOSType() == OS.OSType.Windows) {
            throw new UnsupportedOperationException("Unsupported OS windows");
        }
        profiler = new PosixFlameProfiler();
        this.delegate = profiler;
    }

    private static class SingletonHolder {
        private static final FlameProfiler PROFILER = new FlameProfilerProxy();
    }

    @Override
    public void start() {
        this.delegate.start();
    }

    @Override
    public Path stop() {
        return this.delegate.stop();
    }

    @Override
    public void setInclude(String include) {
        this.delegate.setInclude(include);
    }

    @Override
    public void setExclude(String exclude) {
        this.delegate.setExclude(exclude);
    }

    @Override
    public void setOutput(String exclude) {
        this.delegate.setOutput(exclude);
    }

    @Override
    public void reset() {
        this.delegate.reset();
    }

    public static FlameProfiler getProfiler() {
        return SingletonHolder.PROFILER;
    }
}
