package me.zyee.java.profiler.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Queue;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.ProfileNode;
import me.zyee.java.profiler.ProfilerCore;
import me.zyee.java.profiler.Result;
import me.zyee.java.profiler.Runner;
import me.zyee.java.profiler.annotation.Atoms;
import me.zyee.java.profiler.flame.FlameParser;
import me.zyee.java.profiler.flame.Frame;
import one.profiler.Events;

/**
 * TODO 实现
 *
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
                final Atoms atoms = item.getAtoms();
                final Atoms.Atom[] value = atoms.value();
                final Map<String, Frame> parse = FlameParser.parse(flamePath);
                final Queue<ProfileNode> nodes = item.getNodes();
                ProfileNode root = new ProfileNode();
                root.setName("Profile");

            }
        }
    }
}
