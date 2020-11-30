package me.zyee.java.profiler;

import java.nio.file.Path;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/11/30
 */
public class ProfilerProxy implements Profiler {

    private final Profiler delegate;

    public ProfilerProxy() {
        Profiler profiler;
        if (OS.getOSType() == OS.OSType.Windows) {
            throw new UnsupportedOperationException("Unsupported OS windows");
        }
        profiler = new PosixProfiler();
        this.delegate = profiler;
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
}
