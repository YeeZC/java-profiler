package me.zyee.profiler.agent.event.handler;

import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.listener.EventListener;
import me.zyee.profiler.spy.SpyHandler;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface EventHandler extends SpyHandler {
    void register(int id, EventListener listener, Event.Type[] types);

    void unRegister(int id);
}
