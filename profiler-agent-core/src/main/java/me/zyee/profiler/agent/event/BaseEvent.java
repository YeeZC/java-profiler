package me.zyee.profiler.agent.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class BaseEvent implements Event {
    private final Type type;
    private final Integer id;


    BaseEvent(Builder builder) {
        this.type = builder.type;
        this.id = builder.id;
    }

    @Override
    public Type type() {
        return type;
    }


    public Type getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public static class Builder<T extends Builder<?>> {
        private Type type;
        private Integer id;

        Builder() {
        }

        public T setType(Type type) {
            this.type = type;
            return (T) this;
        }

        public T setId(Integer id) {
            this.id = id;
            return (T) this;
        }

        public T of(BaseEvent baseEvent) {
            this.type = baseEvent.type;
            this.id = baseEvent.id;
            return (T) this;
        }
    }
}
