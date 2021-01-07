package me.zyee.java.profiler.event.watcher;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.filter.BehaviorFilter;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public interface EventWatcher {
    /**
     * 开始观察
     *
     * @param filter   行为匹配
     * @param listener 监听器
     * @param types    监听事件类型
     * @return id
     */
    int watch(BehaviorFilter filter, EventListener listener, Event.Type... types);

    /**
     * 开始观察，监听所有事件
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @return id
     */
    int watch(BehaviorFilter pattern, EventListener listener);

    /**
     * 移除观察
     *
     * @param id 观察id
     */
    void delete(int id);
}
