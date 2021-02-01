package me.zyee.java.profiler.agent.event.handler;

import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;
import me.zyee.java.profiler.spy.SpyHandler;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface EventHandler extends SpyHandler {
    void register(int id, EventListener listener, Event.Type[] types, boolean checkWarmup);

    void unRegister(int id);
}
