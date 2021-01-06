package me.zyee.profiler.agent.event.listener;

import me.zyee.profiler.agent.event.Event;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class EventListenerWrapper implements EventListener {
    private final EventListener delegate;
    private final Event.Type[] types;

    private EventListenerWrapper(Builder builder) {
        this.delegate = builder.delegate;
        this.types = builder.types;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean onEvent(Event event) throws Throwable {
        final Event.Type type = event.type();
        if (ArrayUtils.contains(types, type)) {
            return delegate.onEvent(event);
        }
        return false;
    }


    public EventListener getDelegate() {
        return delegate;
    }

    public Event.Type[] getTypes() {
        return types;
    }

    public static class Builder {
        private EventListener delegate;
        private Event.Type[] types;

        private Builder() {
        }

        public Builder setDelegate(EventListener delegate) {
            this.delegate = delegate;
            return this;
        }

        public Builder setTypes(Event.Type[] types) {
            this.types = types;
            return this;
        }

        public Builder of(EventListenerWrapper eventListenerWrapper) {
            this.delegate = eventListenerWrapper.delegate;
            this.types = eventListenerWrapper.types;
            return this;
        }

        public EventListenerWrapper build() {
            return new EventListenerWrapper(this);
        }
    }
}
