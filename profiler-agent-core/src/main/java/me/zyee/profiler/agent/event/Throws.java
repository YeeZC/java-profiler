package me.zyee.profiler.agent.event;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/6
 */
public class Throws extends BaseEvent {
    private final Throwable throwable;


    private Throws(Builder builder) {
        super(builder.setType(Type.THROWS));
        this.throwable = builder.throwable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static class Builder extends BaseEvent.Builder<Builder> {
        private Throwable throwable;

        public Builder setReturnObject(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder of(Throws obj) {
            super.of(obj);
            this.throwable = obj.throwable;
            return this;
        }

        public Throws build() {
            return new Throws(this);
        }
    }
}
