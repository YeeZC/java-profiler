package me.zyee.java.profiler.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.Task;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import one.profiler.Events;

/**
 * TODO 实现
 *
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
    public void profile(Runner runner) throws IOException {
        Context context = ContextHelper.newContext(runner.name(), Events.CPU);
        assert context != null;
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
            final Queue<ProfileItem> profileItems = context.getProfileItems();
            while (profileItems.peek() != null) {
                final ProfileItem item = profileItems.poll();
                System.out.println(item.getFlamePath());
                if (item.getThrowable() == null) {
                    final Path flamePath = item.getFlamePath();
                    final Map<String, Frame> parse = FlameParser.parse(flamePath);
//                    MarkdownProfileResult profileResult = new MarkdownProfileResult(item.getCost(), new ProfileNode(),
//                            Paths.get(System.getProperty("user.dir"), item.getProfileName() + ".md"));
//                    profileResult.setFrames(parse);
                }
            }
            for (Task task : finished) {
                task.apply(context);
            }
        } else {
            for (Task task : failed) {
                task.apply(context);
            }
        }
    }

}
