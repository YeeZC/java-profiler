package me.zyee.java.profiler;

import me.zyee.java.profiler.impl.ContextHelper;
import one.profiler.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public abstract class BaseProfilerCore implements ProfilerCore {
    private final List<Task> before = new ArrayList<>();
    private final List<Task> after = new ArrayList<>();
    private final List<Task> failed = new ArrayList<>();
    private final List<Task> finished = new ArrayList<>();

    @Override
    public void profile(Runner runner) {
        Context context = ContextHelper.newContext(runner.name(), Events.CPU);
        for (Task task : before) {
            final Result result = task.apply(context);
            if (!result.isOk()) {
                throw new RuntimeException(result.getThrowable());
            }
        }
        final Result result = runner.apply(context);
        if (result.isOk()) {
            for (Task task : after) {
                task.apply(context);
            }
            for (Task task : finished) {
                task.apply(context);
            }
        } else {
            for (Task task : failed) {
                task.apply(context);
            }
        }

        final Queue<ProfileItem> profileItems = context.getProfileItems();
        // TODO 实现报告
    }

}
