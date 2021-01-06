package me.zyee.profiler.agent.event.watcher;

import me.zyee.profiler.agent.event.Event;
import me.zyee.profiler.agent.event.handler.EventHandler;
import me.zyee.profiler.agent.event.listener.EventListener;

import java.lang.instrument.Instrumentation;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class DefaultEventWatcher implements EventWatcher {
    private final Instrumentation inst;
    private final EventHandler handler;

    public DefaultEventWatcher(Instrumentation inst, EventHandler handler) {
        this.inst = inst;
        this.handler = handler;
    }

    @Override
    public int watch(String pattern, EventListener listener, Event.Type... types) {

        return 0;
    }
}
