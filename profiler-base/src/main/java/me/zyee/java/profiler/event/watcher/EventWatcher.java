package me.zyee.java.profiler.event.watcher;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.filter.CallBeforeFilter;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public interface EventWatcher {
    int watch(BehaviorFilter filter, EventListener listener, Event.Type... types);

    /**
     * 开始观察，监听所有事件
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @return id
     */
    int watch(BehaviorFilter pattern, EventListener listener);

    int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener);

    int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener, Event.Type... types);

    /**
     * 移除观察
     *
     * @param id 观察id
     */
    void delete(int id);
}
