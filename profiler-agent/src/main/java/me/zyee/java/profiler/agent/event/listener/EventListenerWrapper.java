package me.zyee.java.profiler.agent.event.listener;

import java.util.Optional;
import me.zyee.java.profiler.agent.Injector;
import me.zyee.java.profiler.event.Event;
import me.zyee.java.profiler.event.listener.EventListener;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class EventListenerWrapper implements EventListener {
    private final EventListener delegate;

    public EventListenerWrapper(EventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean onEvent(Event event) throws Throwable {
        if (!Optional.ofNullable(Injector.isWarmup).orElse(() -> false).get()) {
            return delegate.onEvent(event);
        }
        return true;
    }


    public EventListener getDelegate() {
        return delegate;
    }
}
