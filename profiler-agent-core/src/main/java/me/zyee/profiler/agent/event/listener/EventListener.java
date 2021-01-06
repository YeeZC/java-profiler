package me.zyee.profiler.agent.event.listener;

import me.zyee.profiler.agent.event.Event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public interface EventListener {
    boolean onEvent(Event event) throws Throwable;
}
