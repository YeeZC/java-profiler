package me.zyee.profiler.agent.event.watcher;

import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.listener.EventListener;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public interface EventWatcher {
    /**
     * 开始观察
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @param types    监听事件类型
     * @return id
     */
    int watch(String pattern, EventListener listener, Event.Type... types);

    /**
     * 开始观察，监听所有事件
     *
     * @param pattern  行为匹配
     * @param listener 监听器
     * @return id
     */
    default int watch(String pattern, EventListener listener) {
        return watch(pattern, listener, Event.Type.values());
    }

    /**
     * 移除观察
     *
     * @param id 观察id
     */
    void delete(int id);
}
