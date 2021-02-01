package me.zyee.java.profiler.agent.event.watcher;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.event.watcher.EventWatcher;
import me.zyee.java.profiler.filter.BehaviorFilter;
import me.zyee.java.profiler.filter.CallBeforeFilter;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/2/1
 */
public interface AgentEventWatcher extends EventWatcher {
    /**
     * 开始观察
     *
     * @param filter   行为匹配
     * @param listener 监听器
     * @param types    监听事件类型
     * @return id
     */
    int watch(BehaviorFilter filter, EventListener listener, boolean ifWarmup, Event.Type... types);

    /**
     * 开始观察，监听所有事件
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @return id
     */
    int watch(BehaviorFilter pattern, EventListener listener, boolean ifWarmup);

    int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener, boolean ifWarmup);

    int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener, boolean ifWarmup, Event.Type... types);

    @Override
    default int watch(BehaviorFilter filter, EventListener listener, Event.Type... types) {
        return watch(filter, listener, true, types);
    }

    /**
     * 开始观察，监听所有事件
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @return id
     */
    @Override
    default int watch(BehaviorFilter pattern, EventListener listener) {
        return watch(pattern, listener, true);
    }

    @Override
    default int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener) {
        return watch(filler, callBefore, listener, true);
    }

    @Override
    default int watch(BehaviorFilter filler, CallBeforeFilter callBefore, EventListener listener, Event.Type... types) {
        return watch(filler, callBefore, listener, true, types);
    }
}
