package me.zyee.java.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfilerCore implements ProfilerCore {
    private List<Task> before = new ArrayList<>();
    private List<Task> after = new ArrayList<>();
    private List<Task> failed = new ArrayList<>();
    private List<Task> finished = new ArrayList<>();

    @Override
    public Future<?> profile(Runner runner) {


        return null;
    }
}
