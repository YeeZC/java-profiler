package me.zyee.java.profiler.module;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.DefaultBehaviorFilter;

import javax.annotation.Resource;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/14
 */
public class ActualCostCountModule implements Module {

    @Resource
    private EventWatcher watcher;

    private int watchId;

    private final String pattern;

    private final LongAdder reference;

    public ActualCostCountModule(String pattern) {
        this.pattern = pattern;
        this.reference = new LongAdder();
    }

    @Override
    public void enable() {
        this.watchId = watcher.watch(new DefaultBehaviorFilter(pattern), event -> {
            reference.increment();
            return false;
        }, Event.Type.BEFORE);
    }

    @Override
    public void disable() {
        watcher.delete(watchId);
    }

    public String getPattern() {
        return pattern;
    }

    public Supplier<Long> getReference() {
        return reference::longValue;
    }
}
