package me.zyee.java.profiler.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Queue;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import one.profiler.Events;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2020/12/15
 */
public class DefaultProfilerCore implements ProfilerCore {
    @Override
    public void profile(Runner runner) throws IOException {
        final Context context = ContextHelper.newContext(runner.name(), Events.CPU);
        if (null == context) {
            throw new UnsupportedOperationException();
        }

        final Result apply = runner.apply(context);
        if (apply.isOk()) {
            final Queue<ProfileItem> items = context.getProfileItems();
            while (items.peek() != null) {
                final ProfileItem item = items.poll();
                final Path flamePath = item.getFlamePath();
            }
        }
    }
}
