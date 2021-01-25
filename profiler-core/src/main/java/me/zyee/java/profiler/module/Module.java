package me.zyee.java.profiler.module;

import me.zyee.java.profiler.event.watcher.EventWatcher;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public interface Module {
    void enable();

    void disable();

    EventWatcher getWatcher();
}
