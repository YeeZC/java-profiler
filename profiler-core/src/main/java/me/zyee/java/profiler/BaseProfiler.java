package me.zyee.java.profiler;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfiler implements Profiler {
    protected List<ProfilerListener> prepared;
    protected List<ProfilerListener> before;
    protected List<ProfilerListener> after;
    protected List<ProfilerListener> clean;
    protected List<ProfilerListener> failed;
    protected List<ProfilerListener> finish;

    public void setPrepared(List<ProfilerListener> prepared) {
        this.prepared = ImmutableList.copyOf(prepared);
    }

    public void setBefore(List<ProfilerListener> before) {
        this.before = ImmutableList.copyOf(before);
    }

    public void setAfter(List<ProfilerListener> after) {
        this.after = ImmutableList.copyOf(after);
    }

    public void setClean(List<ProfilerListener> clean) {
        this.clean = ImmutableList.copyOf(clean);
    }

    public void setFailed(List<ProfilerListener> failed) {
        this.failed = ImmutableList.copyOf(failed);
    }

    public void setFinish(List<ProfilerListener> finish) {
        this.finish = ImmutableList.copyOf(finish);
    }

    @Override
    public Future<?> profile(Class<?>... testCases) {
        Context context = null;
        Result handle = handle(context, prepared);
        if (!handle.isOk()) {
            throw new IllegalStateException();
        }
        handle = handle(context, before);
        if (!handle.isOk()) {
            throw new IllegalStateException();
        }
        handle = doProfile(testCases);
        if (!handle.isOk()) {
            handle(context, clean);
            return null;
        }
        handle(context, after);
        handle(context, clean);
        return null;
    }

    protected abstract Result doProfile(Class<?>... testCases);

    private Result handle(Context context, List<ProfilerListener> listeners) {
        for (ProfilerListener listener : listeners) {
            final Result apply = listener.apply(context);
            if (!apply.isOk()) {
                return apply;
            }
        }
        return () -> true;
    }
}
