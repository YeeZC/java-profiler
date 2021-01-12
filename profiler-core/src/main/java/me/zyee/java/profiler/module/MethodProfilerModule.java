package me.zyee.java.profiler.module;

import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Resource;
import me.zyee.java.profiler.Context;
import me.zyee.java.profiler.ProfileHandler;
import me.zyee.java.profiler.ProfileHandlerRegistry;
import me.zyee.java.profiler.ProfileItem;
import me.zyee.java.profiler.Profiler;
import me.zyee.java.profiler.event.Before;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.Throws;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.ProfileBehaviorFilter;
import me.zyee.java.profiler.impl.ContextHelper;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class MethodProfilerModule implements Module {

    @Resource
    private EventWatcher watcher;

    private int watchId;
    private long start;
    private ProfileItem item;
    private Context context;
    private Profiler profiler;

    @Override
    public void enable() {
        this.watchId = watcher.watch(new ProfileBehaviorFilter(), event -> {
            switch (event.type()) {
                case BEFORE:
                    return onBefore((Before) event);
                case RETURN:
                    return onReturn();
                case THROWS:
                    return onThrows((Throws) event);
                default:
            }
            return false;
        }, Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS);
    }

    @Override
    public void disable() {
        watcher.delete(watchId);
    }

    private boolean onBefore(Before before) {
        context = ContextHelper.getContext().resolve(before.getTriggerMethod()
                + System.currentTimeMillis());
        item = new ProfileItem(before.getTriggerMethod());
        profiler = context.getProfiler();
        if (null != profiler) {
            profiler.start();
            start = System.currentTimeMillis();
        }
        return false;
    }

    private boolean onReturn() {
        if (null != profiler) {
            try {
                final ProfileHandler handler = ProfileHandlerRegistry.getHandler();
                item.offer(handler.next());
                item.setCost(System.currentTimeMillis() - start);
                final Path stop = profiler.stop();
                item.setFlamePath(stop);
                Optional.ofNullable(context.getProfileItems()).ifPresent(queue -> queue.offer(item));
                System.out.println("profile tree 输出路径 " + stop);
            } catch (NullPointerException e) {
                item.setThrowable(e);
            }
        }
        return false;
    }

    private boolean onThrows(Throws throwsEvent) {
        item.setThrowable(throwsEvent.getThrowable());
        return onReturn();
    }
}
